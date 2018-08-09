package de.quinscape.domainql.skat.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShufflingCall
{
    private final Random random;

    private final List<Integer> cards;

    private final List<Integer> output;


    public ShufflingCall(Random random, List<Integer> cards,  List<Integer> output)
    {
        this.random = random;
        this.cards = cards;
        this.output = output;
    }




    public Random getRandom()
    {
        return random;
    }


    public List<Integer> getCards()
    {
        return cards;
    }


    public List<Integer> getOutput()
    {
        return output;
    }
}
