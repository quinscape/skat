package de.quinscape.domainql.skat.runtime.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Shuffles one round riffle shuffle with an imperfect split.
 */
public class RiffleShuffle
    implements ShufflingStrategy
{

    private final static Logger log = LoggerFactory.getLogger(RiffleShuffle.class);

    private final double variance;


    public RiffleShuffle()
    {
        this(SplitShuffle.DEFAULT_VARIANCE);
    }


    public RiffleShuffle(double variance)
    {
        this.variance = variance;
    }


    @Override
    public List<Integer> shuffle(Random random, List<Integer> cards)
    {
        log.debug("Do a riffle shuffle with variance = {}", variance);

        List<Integer> copy = new ArrayList<>(cards.size());
        final int split = SplitShuffle.getSplit(random, variance);

        log.debug("Riffle split = {}", split);

        int indexA = 0;
        int indexB = split;

        boolean flag = random.nextBoolean();
        for (int i = 0; i < cards.size(); i++)
        {
            if (flag)
            {
                if (indexA < cards.size())
                {
                    copy.add(cards.get(indexA++));
                }
                else
                {
                    copy.add(cards.get(indexB++));
                }
            }
            else
            {
                if (indexB < cards.size())
                {
                    copy.add(cards.get(indexB++));
                }
                else
                {
                    copy.add(cards.get(indexA++));
                }
            }

            flag = !flag;
        }
        return copy;
    }


    @Override
    public String describe()
    {
        return "RiffleShuffle(variance =  " + variance + ")";
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "variance = " + variance
            ;
    }
}
