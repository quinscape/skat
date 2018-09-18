package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.model.GameDeclaration;
import de.quinscape.domainql.skat.model.channel.Channel;
import de.quinscape.domainql.skat.model.channel.LogEntry;
import de.quinscape.domainql.skat.model.core.Bidding;
import de.quinscape.domainql.skat.model.core.GameOptions;
import de.quinscape.domainql.skat.model.core.GamePhase;
import de.quinscape.domainql.skat.model.core.GameRound;
import de.quinscape.domainql.skat.model.core.GameType;
import de.quinscape.domainql.skat.model.core.Position;
import de.quinscape.domainql.skat.model.core.SkatHand;
import de.quinscape.domainql.skat.model.user.GameUser;
import de.quinscape.domainql.skat.runtime.game.HandFetcher;
import de.quinscape.domainql.skat.runtime.game.JavaCollectionsShuffle;
import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;
import de.quinscape.domainql.skat.runtime.message.PreparedMessages;
import de.quinscape.domainql.skat.util.Cards;
import de.quinscape.domainql.skat.ws.SkatClientConnection;
import de.quinscape.domainql.skat.ws.SkatWebSocketHandler;
import de.quinscape.domainql.annotation.GraphQLLogic;
import de.quinscape.domainql.annotation.GraphQLMutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Handles the core logic of the game.
 */
@GraphQLLogic
public class CoreGameLogic
{
    private final static Logger log = LoggerFactory.getLogger(CoreGameLogic.class);

    private final ChannelRepository channelRepository;

    private final Random random;

    private final SkatWebSocketHandler skatWebSocketHandler;

    private final ShufflingService shufflingService;

    @GraphQLMutation
    public Channel createGame(SkatClientConnection conn, String secret, boolean isPublic)
    {
        final String connectionId = conn.getConnectionId();

        log.debug("createGame(secret = {}, connectionId = {}, isPublic = {})", secret, connectionId, isPublic);

        Channel channel = new Channel(secret);
        channel.setPublic(isPublic);


        final ShufflingStrategy strategy = shufflingService.getStrategy(
            GameOptions.DEFAULT_SHUFFLING_STRATEGY_NAME
        );

        log.debug("Shuffling using {}", strategy);


        final GameRound gameRound = GameRound.shuffleDeck(strategy, random, 0, Cards.INITIAL_DECK, GameOptions.DEFAULT_OPTIONS);

        log.debug("First round: {}", gameRound);


        final AppAuthentication auth = AppAuthentication.current();

        channel.setCurrent(gameRound);
        final ArrayList<String> owners = new ArrayList<>();
        owners.add(auth.getLogin());
        channel.setOwners(owners);

        channelRepository.updateChannel(channel);

        return channel;
    }

    private final static ShufflingStrategy PRE_SHUFFLE = new JavaCollectionsShuffle();

    @Autowired
    public CoreGameLogic(
        ChannelRepository channelRepository,
        Random random,
        SkatWebSocketHandler skatWebSocketHandler,
        ShufflingService shufflingService
    )
    {
        this.channelRepository = channelRepository;
        this.random = random;
        this.skatWebSocketHandler = skatWebSocketHandler;
        this.shufflingService = shufflingService;
    }


    @GraphQLMutation
    public boolean deal(SkatClientConnection conn, String secret)
    {
        final String connectionId = conn.getConnectionId();
        final Channel channel = channelRepository.getChannelById(secret);


        final GameRound current = channel.getCurrent();
        final List<GameUser> seating = current.getSeating();
        final int seatIndex = getSeat(connectionId, seating);


        ensureOpen(current);

        final Position currentPosition = HandFetcher.getCurrentPosition(
            seatIndex,
            current.getCurrentDealer(),
            current.getNumberOfSeats()
        );

        if (currentPosition != Position.DEAL)
        {
            throw new GameLogicException("Current user is not dealer");
        }

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            current.setPhase(GamePhase.BIDDING);
            current.setLastUpdated(Instant.now().toString());
            preparedMessages = channel.prepareUpdate(
                null,
                LogEntry.action(seating.get(seatIndex).getName(), "dealt all cards.")
            );
            channelRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }


    private void ensureOpen(GameRound current)
    {
        if (current.getPhase() != GamePhase.OPEN)
        {
            throw new GameLogicException("Game not open");
        }
    }


    @GraphQLMutation
    public boolean accept(SkatClientConnection conn, String secret, int value)
    {

        final String connectionId = conn.getConnectionId();
        final Channel channel = channelRepository.getChannelById(secret);

        final GameRound current = channel.getCurrent();
        final int numberOfSeats = current.getNumberOfSeats();
        final int seatIndex = getSeat(connectionId, current.getSeating());
        if (current.getPhase() != GamePhase.BIDDING)
        {
            throw new IllegalStateException("Game not in BIDDING phase");
        }

        if (seatIndex < 0)
        {
            throw new IllegalStateException("User has no seat");
        }

        final Position currentPosition = HandFetcher.getCurrentPosition(
            seatIndex,
            current.getCurrentDealer(),
            numberOfSeats
        );

        final Bidding bidding = current.getBidding();

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            final Bidding newBidding = bidding.accepted(currentPosition, value);

            // declarer might have been decided by the dealer accepting 18 after both bid and respond passed.
            if (newBidding.getDeclarer() != null)
            {
                current.setPhase(GamePhase.DECLARING);
            }

            current.setBidding(newBidding);
            current.setLastUpdated(Instant.now().toString());
            final boolean isBidder = currentPosition == newBidding.getBidder();
            preparedMessages = channel.prepareUpdate(
                null,
                LogEntry.action(
                    current.getSeating().get(seatIndex).getName(),
                     (isBidder ? " bids " : " accepts ") + value
                )
            );
            channelRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }


    @GraphQLMutation
    public boolean pass(SkatClientConnection conn, String secret)
    {
        log.debug("pass({},{})", conn, secret);
        final String connectionId = conn.getConnectionId();

        final Channel channel = channelRepository.getChannelById(secret);

        final GameRound current = channel.getCurrent();
        final int seatIndex = getSeat(connectionId, current.getSeating());


        final int numberOfSeats = current.getNumberOfSeats();
        if (current.getPhase() != GamePhase.BIDDING)
        {
            throw new GameLogicException("Current phase must be BIDDING, is: " + current.getPhase());
        }

        if (seatIndex < 0)
        {
            throw new GameLogicException("Current user has no seat");
        }

        Position currentPosition = HandFetcher
            .getCurrentPosition(
                seatIndex, current.getCurrentDealer(), numberOfSeats);

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            final Bidding newBidding = current.getBidding().pass(currentPosition);

            current.setBidding(newBidding);
            current.setLastUpdated(Instant.now().toString());

            if (newBidding.getDeclarer() != null)
            {
                if (newBidding.getDeclarer() == Position.CONTINUE)
                {
                    if (current.getOptions().isAllowRamsch())
                    {
                        final List<Integer> skat = HandFetcher.getSkat(current.getInitialStackInternal());

                        final GameDeclaration ramschDeclaration = new GameDeclaration(
                            GameType.RAMSCH.ordinal(),
                            -1,
                            null,
                            true,
                            false
                        );

                        current.setGameDeclaration(
                            ramschDeclaration
                        );
                        current.getDeclarerWonCards().addAll(skat);
                        current.setPhase(GamePhase.PLAYING);
                    }
                    else
                    {
                        // game found not bidder and no ramsch
                        current.setPhase(GamePhase.FINISHED);
                    }
                }
                else
                {
                    current.setPhase(GamePhase.DECLARING);
                }
            }
            preparedMessages = channel.prepareUpdate(
                null,
                LogEntry.action(
                    current.getSeating().get(seatIndex).getName(),
                    " passes."
                )
            );
            channelRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }


    @GraphQLMutation
    public boolean sit(SkatClientConnection conn, String secret, int seatIndex, String userName)
    {

        final Channel channel = channelRepository.getChannelById(secret);

        final GameRound current = channel.getCurrent();
        final List<GameUser> seating = current.getSeating();
        final String connectionId = conn.getConnectionId();
        final int currentSeat = getSeat(connectionId, seating);

        PreparedMessages preparedMessages = null;

        ensureOpen(current);

        synchronized (channel)
        {
            if (currentSeat > 0)
            {
                seating.set(currentSeat, null);
            }

            seating.set(seatIndex, GameUser.fromAuth(conn.getAuth(), conn.getConnectionId(), userName));
            preparedMessages = channel.prepareUpdate(
                null,
                LogEntry.action(
                    seating.get(currentSeat).getName(),
                     " sits on Seat #" + seatIndex + "."
                )
            );
            channelRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }


    @GraphQLMutation
    public boolean reshuffle(SkatClientConnection conn, String secret)
    {

        final Channel channel = channelRepository.getChannelById(secret);
        final String connectionId = conn.getConnectionId();

        final GameRound current = channel.getCurrent();
        final List<GameUser> seating = current.getSeating();
        final int seatIndex = getSeat(connectionId, seating);


        ensureOpen(current);

        final Position currentPosition = HandFetcher.getCurrentPosition(
            seatIndex,
            current.getCurrentDealer(),
            current.getNumberOfSeats()
        );

        if (currentPosition != Position.DEAL)
        {
            throw new IllegalStateException("User is not the dealer");
        }

        final ArrayList<Integer> newStack = new ArrayList<>(current.getInitialStackInternal());
        Collections.shuffle(newStack, random);

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            current.setInitialStack(newStack);
            current.setLastUpdated(Instant.now().toString());
            preparedMessages = channel.prepareUpdate(
                null,
                LogEntry.action(
                    seating.get(seatIndex).getName(),
                    "*shuffles*"
                )
            );
            channelRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }


    @GraphQLMutation
    public boolean pickUpSkat(SkatClientConnection conn, String secret)
    {

        final Channel channel = channelRepository.getChannelById(secret);
        final String connectionId = conn.getConnectionId();
        final GameRound current = channel.getCurrent();

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            final int seatIndex = getSeat(connectionId, current.getSeating());

            if (
                current.getPhase() == GamePhase.DECLARING &&
                    seatIndex >= 0 &&
                    HandFetcher.getCurrentPosition(
                        seatIndex,
                        current.getCurrentDealer(),
                        current.getNumberOfSeats()
                    ) == current.getBidding().getDeclarer()
            )
            {
                current.getBidding().setSkatPickedUp(true);
                preparedMessages = channel.prepareUpdate(
                    null,
                    LogEntry.action(
                        current.getSeating().get(seatIndex).getName(),
                        "picked up the skat."
                    )
                );
            }

        }

        if (preparedMessages != null)
        {
            preparedMessages.sendAll(skatWebSocketHandler);
            channelRepository.updateChannel(channel);
        }
        return true;
    }


    @GraphQLMutation
    public boolean declareGame(
        SkatClientConnection conn,
        String secret,
        GameDeclaration gameDeclaration,
        int skatA,
        int skatB
    )
    {
        if (!gameDeclaration.isValid())
        {
            throw new GameLogicException("Invalid game declaration: " + gameDeclaration);
        }

        final Channel channel = channelRepository.getChannelById(secret);
        final String connectionId = conn.getConnectionId();
        final GameRound current = channel.getCurrent();

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            final int seatIndex = getSeat(connectionId, current.getSeating());

            if (
                current.getPhase() == GamePhase.DECLARING &&
                    seatIndex >= 0 &&
                    HandFetcher.getCurrentPosition(
                        seatIndex,
                        current.getCurrentDealer(),
                        current.getNumberOfSeats()
                    ) == current.getBidding().getDeclarer()
            )
            {
                final SkatHand hand = HandFetcher.getHand(current, connectionId);

                final List<Integer> filteredCards = hand.getCards().stream()
                    .filter(i -> !(i == skatA || i == skatB))
                    .collect(Collectors.toList());

                if (filteredCards.size() != 10)
                {
                    throw new GameLogicException("Must have exactly 10 cards after dropping skat");
                }

                final int value = gameDeclaration.getGameValue(filteredCards);
                if (value < current.getBidding().getCurrentBid())
                {
                    throw new GameLogicException(
                        "Value of declared game must be at least as high as the current bid: is " + value + ", must " +
                            "be > " + current
                            .getBidding().getCurrentBid());
                }

                final String description = gameDeclaration.getDescription(filteredCards);
                if (description == null)
                {
                    throw new GameLogicException(
                        "Invalid game type: " + gameDeclaration + " for cards = " + filteredCards);
                }

                current.setGameDeclaration(gameDeclaration);
                current.setPhase(GamePhase.PLAYING);
                current.setNextPlayer(Position.RESPOND);

                final List<Integer> declarerWonCards = new ArrayList<>(current.getDeclarerWonCardsInternal());
                declarerWonCards.add(skatA);
                declarerWonCards.add(skatB);
                current.setDeclarerWonCards(declarerWonCards);

                preparedMessages = channel.prepareUpdate(
                    null,
                    LogEntry.action(
                        current.getSeating().get(seatIndex).getName(),
                     " declares " + description
                    )
                );
            }
        }

        if (preparedMessages != null)
        {
            preparedMessages.sendAll(skatWebSocketHandler);

            channelRepository.updateChannel(channel);
        }

        return true;
    }


    @GraphQLMutation
    public boolean playCard(
        SkatClientConnection conn,
        String secret,
        final int card
    )
    {

        final Channel channel = channelRepository.getChannelById(secret);
        final String connectionId = conn.getConnectionId();
        final GameRound current = channel.getCurrent();

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            final int seatIndex = getSeat(connectionId, current.getSeating());

            final SkatHand hand = HandFetcher.getHand(current, connectionId);
            if (current.getPhase() != GamePhase.PLAYING)
            {
                throw new IllegalStateException("Game not in PLAYING phase");
            }
            if (hand == null)
            {
                throw new IllegalStateException("User has no hand of cards");
            }

            final int numberOfSeats = current.getNumberOfSeats();
            final Position currentPosition = HandFetcher.getCurrentPosition(
                seatIndex,
                current.getCurrentDealer(),
                numberOfSeats
            );

            final List<Integer> trick = current.getTrick();
            final Position playPosition = current.getNextPlayer();

            if (currentPosition == playPosition)
            {
                final List<Integer> newHand = new ArrayList<>(hand.getCards());
                final int index = newHand.indexOf(card);
                if (index < 0)
                {
                    throw new IllegalStateException("Played card (" + Cards.cardDescription(card) + ") not found in hand.");
                }


                trick.add(card);

                List<LogEntry> actions = new ArrayList<>();

                actions.add(
                    LogEntry.card(
                        current.getSeating().get(seatIndex).getName(),
                        card
                    )
                );


                final List<Integer> removed;
                if (trick.size() == 3)
                {
                    scoreGame(current, trick, actions);
                    trick.clear();

                    channel.getLogEntries().addAll(actions);

                    removed = channel.flushGameLogEntries();

                }
                else
                {
                    current.setNextPlayer(
                        current.getNextPlayer().next(
                            current.getOptions().isAllowContinue()
                        )
                    );
                    removed = null;

                    channel.getLogEntries().addAll(actions);
                }

                final Channel minimized = channel.getMinimizedCopy();
                minimized.getLogEntries().addAll(actions);
                minimized.setRemovedLogEntries(removed);

                preparedMessages = minimized.prepareUpdate(
                    channel.getUsers()
                );
            }
        }

        if (preparedMessages != null)
        {
            preparedMessages.sendAll(skatWebSocketHandler);

            channelRepository.updateChannel(channel);
        }

        return true;
    }


    private int getSeat(String connectionId, List<GameUser> seating)
    {
        final int seatIndex = HandFetcher.findSeatByConnection(seating, connectionId);
        if (seatIndex < 0)
        {
            throw new GameLogicException("Current user has no seat");
        }
        return seatIndex;
    }


    private void scoreGame(GameRound current, List<Integer> trick, List<LogEntry> actions)
    {
        final int winningCard = Cards.getWinningCard(
            trick, GameType.valueOf(current.getGameDeclaration().getGameType()));
        final Position winnerPosition = current.getSeatPosition(current.getNextPlayer().ordinal() - (2 - trick.indexOf(winningCard)));

        if (log.isDebugEnabled())
        {
            log.debug(
                "Winning Card = {}, winning position = {}",
                Cards.cardDescription(winningCard),
                winnerPosition
            );
        }

        if (winnerPosition == current.getBidding().getDeclarer())
        {
            final List<Integer> declarerWonCards = new ArrayList<>(current.getDeclarerWonCardsInternal());
            declarerWonCards.addAll(trick);
            current.setDeclarerWonCards(declarerWonCards);
            actions.add(LogEntry.win("Declarer wins trick."));
        }
        else
        {
            final List<Integer> oppositionWonCards = new ArrayList<>(current.getOppositionWonCardsInternal());
            oppositionWonCards.addAll(trick);
            current.setOppositionWonCards(oppositionWonCards);

            actions.add(LogEntry.win("Opposition wins trick."));
        }

        current.setNextPlayer(winnerPosition);

        final List<Integer> declarerWonCards = current.getDeclarerWonCardsInternal();
        if (declarerWonCards.size() + current.getOppositionWonCardsInternal().size() == 32)
        {
            current.setPhase(GamePhase.FINISHED);

            actions.add(
                LogEntry.skat(declarerWonCards.get(0))
            );
            actions.add(
                LogEntry.skat(declarerWonCards.get(1))
            );

            int points = Cards.score(current.getDeclarerWonCards());

            if (points == 120)
            {
                actions.add(
                    LogEntry.gameWin(
                        "Declarer won 'no-tricks': " + points + " to " + (120 - points)
                    )
                );
            }
            else if (points >= 90)
            {
                actions.add(
                    LogEntry.gameWin(
                        "Declarer won 'schneider': " + points + " to " + (120 - points)
                    )
                );
            }
            else if (points >= 61)
            {
                actions.add(
                    LogEntry.gameWin(
                        "Declarer won: " + points + " to " + (120 - points)
                    )
                );
            }
            else
            {
                actions.add(
                    LogEntry.gameWin(
                        "Declarer lost: " + points + " to " + (120 - points)
                    )
                );
            }
        }
    }


    @GraphQLMutation
    public boolean startNewRound(
        SkatClientConnection conn,
        String secret
    )
    {

        final Channel channel = channelRepository.getChannelById(secret);

        final GameRound current = channel.getCurrent();

        if (current.getPhase() != GamePhase.FINISHED)
        {
            throw new IllegalStateException("Can't start new round with old round not finished");
        }

        final PreparedMessages preparedMessages;
        synchronized (channel)
        {
            final int nextDealer = current.getCurrentDealer() + 1 < current.getNumberOfSeats() ? current
                .getCurrentDealer() + 1 : 0;

            final ShufflingStrategy strategy = shufflingService.getStrategy(
                current.getOptions().getShufflingStrategyName()
            );

            final GameRound gameRound = GameRound.shuffleDeck(
                strategy,
                random,
                nextDealer,
                Cards.collectCards(random, current),
                current.getOptions()
            );
            
            gameRound.setSeating(current.getSeating());
            channel.setCurrent(gameRound);

            preparedMessages = channel.prepareUpdate(null, LogEntry.text(LogEntry.SYSTEM,"Started new round."));


            preparedMessages.sendAll(skatWebSocketHandler);
            channelRepository.updateChannel(channel);

            return true;
        }

    }
}
