package de.fforw.skat.runtime.service;

import de.fforw.skat.model.GamePhase;
import de.fforw.skat.model.GameRound;
import de.fforw.skat.model.GameUser;
import de.fforw.skat.model.GameUserType;
import de.fforw.skat.model.channel.Channel;
import de.fforw.skat.model.channel.ChannelListing;
import de.fforw.skat.model.channel.ChannelListings;
import de.fforw.skat.runtime.ChannelComparator;
import de.fforw.skat.runtime.HandFetcher;
import de.fforw.skat.runtime.config.AppAuthentication;
import de.fforw.skat.runtime.message.PreparedMessages;
import de.fforw.skat.ws.SkatClientConnection;
import de.fforw.skat.ws.SkatWebSocketHandler;
import de.quinscape.domainql.annotation.GraphQLField;
import de.quinscape.domainql.annotation.GraphQLLogic;
import de.quinscape.domainql.annotation.GraphQLMutation;
import de.quinscape.domainql.annotation.GraphQLQuery;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@GraphQLLogic
public class SkatGameLogic
{
    private final static Logger log = LoggerFactory.getLogger(SkatGameLogic.class);

    private final DSLContext dslContext;

    private final GameRepository gameRepository;

    private final Random random;

    private final SkatWebSocketHandler skatWebSocketHandler;


    @Autowired
    public SkatGameLogic(
        DSLContext dslContext,
        GameRepository gameRepository,
        Random random,
        SkatWebSocketHandler skatWebSocketHandler
    )
    {
        this.dslContext = dslContext;
        this.gameRepository = gameRepository;
        this.random = random;
        this.skatWebSocketHandler = skatWebSocketHandler;
    }


    /**
     * the websocket channel updates are routed through this to provide the same authentication related view on
     * the current channel.
     *
     * @param channel channel input object
     * @return
     */
    @GraphQLQuery
    public Channel filterChannel(Channel channel)
    {
        /**
         *  just output, the auth logic is in the specialized fetchers
         *
         * @see GameRound#getInitialStack()
         * @see GameRound#getHand()
         */
        return channel;
    }


    @GraphQLQuery
    public ChannelListings currentGameList(
        @GraphQLField(defaultValue = "0")
            int offset,
        @GraphQLField(defaultValue = "20")
            int limit
    )
    {
        log.debug("currentGameList({}, {})", offset, limit);

        final List<ChannelListing> channels = gameRepository.listPublic();
        final ChannelListings channelListings = new ChannelListings();

        final int rowCount = channels.size();

        channelListings.setRowCount(rowCount);

        channels.sort(new ChannelComparator());


        if (rowCount > limit)
        {
            channelListings.setChannels(channels.subList(offset, offset + limit));
        }
        else
        {
            channelListings.setChannels(channels);
        }

        return channelListings;
    }


    @GraphQLMutation
    public Channel createGame(SkatClientConnection conn, String secret, boolean isPublic)
    {
        final String connectionId = conn.getConnectionId();

        log.debug("createGame(secret = {}, connectionId = {}, isPublic = {})", secret, connectionId, isPublic);

        Channel channel = new Channel(secret);
        channel.setPublic(isPublic);
        final GameRound gameRound = GameRound.shuffleDeck(random);

        final AppAuthentication auth = AppAuthentication.current();

        channel.setCurrent(gameRound);
        final ArrayList<String> owners = new ArrayList<>();
        owners.add(auth.getLogin());
        channel.setOwners(owners);

        gameRepository.updateChannel(channel);

        return channel;
    }


    @GraphQLMutation
    public Channel joinGame(SkatClientConnection conn, String secret)
    {

        final String connectionId = conn.getConnectionId();

        log.debug("joinGame(secret = {}, connectionId = {})", secret, connectionId);

        final AppAuthentication auth = AppAuthentication.current();
        Channel channel = gameRepository.getChannelById(secret);

        if (channel == null)
        {
            throw new IllegalStateException("Channel '" + secret + "' does not exist");
        }

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {

            final List<GameUser> users = channel.getUsers();
            final List<GameUser> seating = channel.getCurrent().getSeating();
            if (
                users.stream().noneMatch(
                    u -> u.getConnectionId().equals(connectionId)
                )
            )
            {
                final GameUser newUser = GameUser.fromAuth(auth, connectionId);

                final GameRound current = channel.getCurrent();
                if (current.getPhase() == GamePhase.OPEN)
                {
                    boolean replaced = false;
                    for (int i = 0; i < users.size(); i++)
                    {
                        GameUser currentUser = users.get(i);

                        // if the current user is inactive and equal to us
                        final boolean currentIsTest = currentUser.getType() == GameUserType.TEST_USER;
                        final boolean newIsTest = newUser.getType() == GameUserType.TEST_USER;
                        if (
                            !currentUser.isActive() && (
                                currentUser.equals(newUser) || (currentIsTest && newIsTest)
                            )
                        )
                        {
                            // we replace them in "users"..
                            users.set(i, newUser);

                            // .. and replace them by name in seating, too.
                            for (int j = 0; j < seating.size(); j++)
                            {
                                GameUser currentSeat = seating.get(j);
                                if (currentSeat.getName().equals(currentUser.getName()))
                                {
                                    seating.set(j, newUser);
                                    break;
                                }
                            }
                            replaced = true;
                            break;
                        }
                    }

                    if (!replaced)
                    {
                        users.add(
                            newUser
                        );

                        final int nullPos = seating.indexOf(null);
                        if (nullPos >= 0)
                        {
                            log.debug("Replace null user at #", nullPos);
                            seating.set(nullPos, newUser);
                        }
                        else if (seating.size() < current.getNumberOfSeats())
                        {
                            log.debug("Add user");
                            seating.add(newUser);
                        }
                    }

                    current.setLastUpdated(Instant.now().toString());

                    preparedMessages = channel.prepareUpdate(newUser, newUser.getName() + " joined the channel");
                }
            }
            gameRepository.updateChannel(channel);
        }

        if (preparedMessages != null)
        {
            preparedMessages.sendAll(skatWebSocketHandler);
        }

        return channel;
    }


    @GraphQLMutation
    public boolean reshuffle(SkatClientConnection conn, String secret)
    {

        final Channel channel = gameRepository.getChannelById(secret);
        final String connectionId = conn.getConnectionId();

        final GameRound current = channel.getCurrent();
        final int seatIndex = HandFetcher.findSeatByConnection(current.getSeating(), connectionId);

        final int currentPosition;

        if (current.getPhase() != GamePhase.OPEN || seatIndex < 0 || (currentPosition = HandFetcher.getCurrentPosition(
            seatIndex, current.getCurrentDealer(), current.getNumberOfSeats())) != 0)
        {
            return false;
        }

        final ArrayList<Integer> newStack = new ArrayList<>(current.getInitialStackInternal());
        Collections.shuffle(newStack, random);

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            current.setInitialStack(newStack);
            current.setLastUpdated(Instant.now().toString());
            preparedMessages = channel.prepareUpdate(null, "*shuffling*");
            gameRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }


    @GraphQLMutation
    public boolean flushGames()
    {
        gameRepository.flush();

        return true;
    }
}
