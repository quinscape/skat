package de.quinscape.domainql.skat.runtime.game;

import java.util.List;
import java.util.Random;


/**
 * No op shuffle.
 */
public class NeutralShuffle
    implements ShufflingStrategy
{
    public final static NeutralShuffle INSTANCE = new NeutralShuffle();

    private NeutralShuffle()
    {

    }


    @Override
    public List<Integer> shuffle(Random random, List<Integer> cards)
    {
        return cards;
    }
}
