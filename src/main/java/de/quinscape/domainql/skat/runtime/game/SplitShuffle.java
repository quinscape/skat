package de.quinscape.domainql.skat.runtime.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Does an imperfect split on a
 */
public class SplitShuffle
    implements ShufflingStrategy
{
    /** 7% of 32 = a bit over two cards */
    protected static final double DEFAULT_VARIANCE = 0.07;

    public static final SplitShuffle DEFAULT = new SplitShuffle(DEFAULT_VARIANCE);

    private final double variance;


    public SplitShuffle(double variance)
    {

        this.variance = variance;
    }

    private final static Logger log = LoggerFactory.getLogger(SplitShuffle.class);




    @Override
    public List<Integer> shuffle(Random random, List<Integer> cards)
    {


        final int split = getSplit(random, variance);

        log.debug("Do a split shuffle at {}", split);

        List<Integer> copy = new ArrayList<>(cards.size());

        copy.addAll(cards.subList(split, cards.size()));
        copy.addAll(cards.subList(0, split));
        return copy;
    }


    @Override
    public String describe()
    {
        return "SplitShuffle(variance = " + variance + ")";
    }


    static int getSplit(Random random, double variance)
    {
        return (int) (16 * (1 + variance/2 +  random.nextDouble() * variance));
    }


    @Override
    public String toString()
    {
        return super.toString();
    }
}
