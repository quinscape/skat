package de.quinscape.domainql.skat.runtime.game;

import de.quinscape.domainql.skat.model.GameDeclaration;
import de.quinscape.domainql.skat.model.core.Bidding;
import de.quinscape.domainql.skat.model.core.GamePhase;
import de.quinscape.domainql.skat.model.core.GameRound;
import de.quinscape.domainql.skat.model.core.GameType;
import de.quinscape.domainql.skat.model.user.GameUser;
import de.quinscape.domainql.skat.model.core.Position;
import de.quinscape.domainql.skat.model.core.SkatHand;
import de.quinscape.domainql.skat.util.Cards;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Specialized fetcher to return the cards for the currently authenticated player or no cards if the player
 * is not part of the game.
 */
public class HandFetcher
    implements DataFetcher<SkatHand>
{
    private final static Logger log = LoggerFactory.getLogger(HandFetcher.class);


    private final String name;

    private final static List<Integer> HIDDEN_HAND = Arrays.asList(
        Cards.FACE_DOWN_CARD,
        Cards.FACE_DOWN_CARD,
        Cards.FACE_DOWN_CARD,
        Cards.FACE_DOWN_CARD,
        Cards.FACE_DOWN_CARD,
        Cards.FACE_DOWN_CARD,
        Cards.FACE_DOWN_CARD,
        Cards.FACE_DOWN_CARD,
        Cards.FACE_DOWN_CARD,
        Cards.FACE_DOWN_CARD
    );

    public HandFetcher(String name)
    {
        this.name = name;
    }


    @Override
    public SkatHand get(DataFetchingEnvironment environment)
    {
        final GameRound gameRound = environment.getSource();
        final String connectionId = environment.getContext();
        return getHand(gameRound, connectionId);

    }


    public static SkatHand getHand(GameRound gameRound, String connectionId)
    {
        final int currentDealer = gameRound.getCurrentDealer();
        
        final List<GameUser> seating = gameRound.getSeating();
        final int index = findSeatByConnection(seating, connectionId);
        if (index < 0)
        {
            return null;
        }

        final Position currentPosition = getCurrentPosition(index, currentDealer, gameRound.getNumberOfSeats());

        return getHand(gameRound, seating.get(index), currentPosition);
    }


    public static SkatHand getHand(GameRound gameRound, GameUser gameUser, Position currentPosition)
    {
        if (log.isDebugEnabled())
        {
            log.debug("getHand: currentPosition = {}", currentPosition);
        }


        final GamePhase phase = gameRound.getPhase();
        if (phase == GamePhase.OPEN)
        {
            log.debug("Hidden hand");
            return new SkatHand(
                HIDDEN_HAND,
                Collections.emptyList(),
                gameUser,
                currentPosition,
                0,
                0
            );
        }

        final int oppositionScore = gameRound.getOppositionScoreInternal();
        final int declarerScore;
        final Bidding bidding = gameRound.getBidding();

        // if the game is over or the current position is the declarer and the skat was picked up
        if (
            phase == GamePhase.FINISHED ||
            (
                currentPosition ==  bidding.getDeclarer() &&
                    bidding.isSkatPickedUp()
            )
        )
        {
            // then they are allowed to know the true score
            declarerScore = gameRound.getDeclarerScoreInternal();

            log.debug("Reveal declarerScore = {}", declarerScore);
        }
        else
        {
            // in every other situation we hide the skat value to not leak information
            declarerScore = gameRound.getDeclarerScoreInternal() - gameRound.getSkatScoreInternal();

            log.debug("Subtract skat value {} from declarerScore = {}", gameRound.getSkatScoreInternal(), declarerScore);
        }

        if (phase == GamePhase.FINISHED)
        {
            log.debug("Hidden hand");
            return new SkatHand(Collections.emptyList(), Collections.emptyList(), gameUser, currentPosition,
                declarerScore, oppositionScore
            );
        }

        final List<Integer> cards = new ArrayList<>(10);

        final List<Integer> initialStack = gameRound.getInitialStackInternal();

        // 3-each, skat, 4-each, 3-each dealing pattern, starting at RESPOND
        switch (currentPosition)
        {
            // 9 and 10 are the skat/der stock
            // Geber
            case DEAL:
                cards.add(initialStack.get(6));
                cards.add(initialStack.get(7));
                cards.add(initialStack.get(8));

                cards.add(initialStack.get(19));
                cards.add(initialStack.get(20));
                cards.add(initialStack.get(21));
                cards.add(initialStack.get(22));

                cards.add(initialStack.get(29));
                cards.add(initialStack.get(30));
                cards.add(initialStack.get(31));
                break;
            // Höhrer
            case RESPOND:
                cards.add(initialStack.get(0));
                cards.add(initialStack.get(1));
                cards.add(initialStack.get(2));

                cards.add(initialStack.get(11));
                cards.add(initialStack.get(12));
                cards.add(initialStack.get(13));
                cards.add(initialStack.get(14));

                cards.add(initialStack.get(23));
                cards.add(initialStack.get(24));
                cards.add(initialStack.get(25));
                break;
            // Sager
            case BID:
                cards.add(initialStack.get(3));
                cards.add(initialStack.get(4));
                cards.add(initialStack.get(5));

                cards.add(initialStack.get(15));
                cards.add(initialStack.get(16));
                cards.add(initialStack.get(17));
                cards.add(initialStack.get(18));

                cards.add(initialStack.get(26));
                cards.add(initialStack.get(27));
                cards.add(initialStack.get(28));
                break;
            // Weiter-Sager
            case CONTINUE:
                // no cards
                break;
            default:
                throw new IllegalStateException("Unhandled enum " + currentPosition);
        }


        final boolean isDeclaring = phase == GamePhase.DECLARING;
        final boolean isPlaying = phase == GamePhase.PLAYING;
        log.debug("isDeclaring = {}, isPlaying = {}", isDeclaring, isPlaying);


        final List<Integer> skat;
        boolean addFaceDownSkat = false;
        final Position declarer = bidding.getDeclarer();
        if (declarer == currentPosition)
        {
            if ((isDeclaring || isPlaying) &&
                bidding.isSkatPickedUp())
            {
                log.debug("Add skat cards");

                skat = getSkat(initialStack);
                cards.add(skat.get(0));
                cards.add(skat.get(1));
            }
            else
            {
                log.debug("Add face-down skat cards");
                addFaceDownSkat = true;
                skat = Arrays.asList(Cards.FACE_DOWN_CARD, Cards.FACE_DOWN_CARD);
            }
        }
        else
        {
            skat = Collections.emptyList();
        }

        if (isPlaying)
        {
            cards.removeAll(gameRound.getDeclarerWonCardsInternal());
            cards.removeAll(gameRound.getOppositionWonCardsInternal());
            cards.removeAll(gameRound.getTrick());
        }

        final GameDeclaration gameDeclaration = gameRound.getGameDeclaration();
        cards.sort(new CardComparator(gameDeclaration != null ? gameDeclaration.getGameType() : GameType.GRAND.ordinal()));

        if (addFaceDownSkat)
        {
            cards.add(Cards.FACE_DOWN_CARD);
            cards.add(Cards.FACE_DOWN_CARD);
        }


        final SkatHand skatHand = new SkatHand(
            cards,
            skat,
            gameUser,
            currentPosition,
            declarerScore,
            oppositionScore
        );

        log.debug("Hand is {}", skatHand);

        return skatHand;
    }


    public static List<Integer> getSkat(List<Integer> initialStack)
    {
        return Arrays.asList(
            initialStack.get(9),
            initialStack.get(10)
        );
    }


    public static int findSeatByConnection(List<GameUser> seating, String connectionId)
    {
        if (seating != null)
        {
            for (int i = 0; i < seating.size(); i++)
            {
                GameUser user = seating.get(i);
                if (user.getConnectionId().equals(connectionId))
                {
                    return i;
                }
            }
        }
        return -1;
    }


    public static Position getCurrentPosition(int seatNr, int dealerIndex, int numSeats)
    {
        int posIndex = (seatNr - dealerIndex);
        if (posIndex < 0)
        {
            posIndex += numSeats;
        }

        return Position.values()[posIndex];
    }
}
