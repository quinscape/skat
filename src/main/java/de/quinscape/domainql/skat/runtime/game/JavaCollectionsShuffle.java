package de.quinscape.domainql.skat.runtime.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Shuffles using the standard Java Collections shuffle method.
 */
public class JavaCollectionsShuffle
    implements ShufflingStrategy
{

    @Override
    public List<Integer> shuffle(Random random, List<Integer> cards)
    {
        final ArrayList<Integer> copy = new ArrayList<>(cards);
        Collections.shuffle(copy, random);
        return copy;
    }


    @Override
    public String describe()
    {
        return "Java Collections.shuffle() based shuffle";
    }
}
