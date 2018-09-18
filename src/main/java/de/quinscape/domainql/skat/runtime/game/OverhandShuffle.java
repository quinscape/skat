package de.quinscape.domainql.skat.runtime.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Does an overhand shuffle. Takes chunks of 1-5 (default) cards of the top of the given deck and arranges them in
 * opposite
 * order.
 */
public class OverhandShuffle
    implements ShufflingStrategy
{

    private final static Logger log = LoggerFactory.getLogger(OverhandShuffle.class);

    private final int maxCards;


    public OverhandShuffle()
    {
        this(5);
    }


    /**
     * Creates a new overhand shuffle
     *
     * @param maxCards      maximum size of the chunk, inklusive.
     */
    public OverhandShuffle(int maxCards)
    {
        this.maxCards = maxCards;
    }


    public int getMaxCards()
    {
        return maxCards;
    }


    @Override
    public List<Integer> shuffle(Random random, List<Integer> cards)
    {
        log.debug("Do an overhand shuffle");

        final int cardCount = cards.size();
        List<Integer> copy = new ArrayList<>(cardCount);

        int pos = cardCount;
        do
        {
            final int chunk = random.nextInt(maxCards) + 1;
            copy.addAll(cards.subList(Math.max(0, pos - chunk), pos));
            pos -= chunk;
        } while (pos >= 0);
        return copy;
    }


    @Override
    public String describe()
    {
        return "OverhandShuffle(maxCards =  " + maxCards + ")";
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + ", maxCards = " + maxCards
            ;
    }
}

