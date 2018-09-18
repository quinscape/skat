package de.quinscape.domainql.skat.runtime;

import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestShuffle
    implements ShufflingStrategy
{
    private final List<ShufflingCall> calls;


    public TestShuffle()
    {
        calls = new ArrayList<>();
    }


    @Override
    public List<Integer> shuffle(Random random, List<Integer> cards)
    {
        final ShufflingCall call = new ShufflingCall(random, cards, shuffle(cards));
        calls.add(call);
        return call.getOutput();
    }


    @Override
    public String describe()
    {
        return "TestShuffle";
    }


    private List<Integer> shuffle(List<Integer> cards)
    {
        final ArrayList<Integer> list = new ArrayList<>(cards.size());
        list.addAll(cards.subList(1, cards.size()));
        list.add(cards.get(0));
        return list;
    }

    public List<ShufflingCall> getCalls()
    {
        return calls;
    }
}
