package de.fforw.skat.runtime;

import de.fforw.skat.model.GamePhase;
import de.fforw.skat.model.GameRound;
import de.fforw.skat.model.GameUser;
import de.fforw.skat.model.Position;
import de.fforw.skat.model.SkatHand;
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
        33,
        33,
        33,
        33,
        33,
        33,
        33,
        33,
        33,
        33
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
        int dealerIndex = gameRound.getCurrentDealer();
        final List<GameUser> seating = gameRound.getSeating();
        final int index = findSeatByConnection(seating, connectionId);

        if (log.isDebugEnabled())
        {
            log.info("GET HAND: {} for connection {}, seat = ", gameRound, connectionId, index < 0 ? null : seating.get(index));
        }

        if (index < 0)
        {
            return null;
        }

        final int currentPosition = getCurrentPosition(index, dealerIndex, seating.size());

        if (gameRound.getPhase() == GamePhase.OPEN)
        {
            return new SkatHand(HIDDEN_HAND, seating.get(index), currentPosition);
        }

        final List<Integer> cards = new ArrayList<>(10);

        final List<Integer> initialStack = gameRound.getInitialStackInternal();

        // 3-each, skat, 4-each, 3-each dealing pattern, starting at RESPOND
        final Position pos = Position.values()[currentPosition];
        switch (pos)
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
                throw new IllegalStateException("Unhandled enum " + pos);
        }


        log.debug("Card before {}", cards);
        cards.sort(new CardComparator());
        log.debug("Card after {}", cards);

        final SkatHand skatHand = new SkatHand(cards, seating.get(index), currentPosition);

        log.debug("Hand is {}", skatHand);

        return skatHand;
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


    public static int getCurrentPosition(int seatNr, int dealerIndex, int numSeats)
    {
        int posIndex = (seatNr - dealerIndex);
        if (posIndex < 0)
        {
            posIndex += numSeats;
        }

        return posIndex;
    }
}
