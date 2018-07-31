package de.fforw.skat.runtime;

import de.fforw.skat.domain.model.GameRound;
import de.fforw.skat.domain.model.Position;
import de.fforw.skat.runtime.config.AppAuthentication;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Specialized fetcher to return the cards for the currently authenticated player or no cards if the player
 * is not part of the game.
 */
public class HandFetcher
    implements DataFetcher<Object>
{
    private final String name;


    public HandFetcher(String name)
    {
        this.name = name;
    }


    @Override
    public Object get(DataFetchingEnvironment environment)
    {
        final boolean isCurrentPosition = name.equals("currentPosition");
        final GameRound gameRound = environment.getSource();
        int dealerIndex = gameRound.getCurrentDealer();
        final String currentLogin = AppAuthentication.current().getLogin();
        final List<String> seating = gameRound.getSeating();
        final int index = seating.indexOf(currentLogin);
        if (index < 0)
        {
            return isCurrentPosition ? -1 : Collections.emptyList();
        }


        int pos = getCurrentPosition(index, dealerIndex, seating.size());
        if (isCurrentPosition)
        {
            return pos;
        }

        List<Integer> cards = new ArrayList<>(10);

        final List<Integer> initialStack = gameRound.getInitialStack();

        // 3-each, skat, 4-each, 3-each dealing pattern, starting at RESPOND
        final Position position = Position.values()[pos];
        switch (position)
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

        return cards;

    }

    static int getCurrentPosition(int seatNr, int dealerIndex, int numSeats)
    {
        int posIndex = (seatNr - dealerIndex);
        if (posIndex < 0)
        {
            posIndex += numSeats;
        }

        return posIndex;
    }
}
