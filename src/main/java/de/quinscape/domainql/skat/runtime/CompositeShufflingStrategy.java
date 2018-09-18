package de.quinscape.domainql.skat.runtime;

import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;
import de.quinscape.domainql.skat.runtime.game.SplitShuffle;
import de.quinscape.domainql.skat.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A complex shuffling strategy composed of repetitions of simpler shuffles. Comes with a fluent builder.
 *
 * @see #builder()
 */
public class CompositeShufflingStrategy
    implements ShufflingStrategy
{
    private final List<ShufflingRepeat> strategies;


    public CompositeShufflingStrategy(List<ShufflingRepeat> strategies)
    {
        this.strategies = strategies;
    }


    @Override
    public List<Integer> shuffle(Random random, List<Integer> cards)
    {
        for (ShufflingRepeat r : strategies)
        {
            final int min = r.getMinTimes();
            final int max = r.getMaxTimes();

            final int delta = max - min;
            final int repeat = min + (delta > 0 ? random.nextInt(delta + 1) : 0);

            for (int i = 0; i < repeat; i++)
            {
                cards = r.getShufflingStrategy().shuffle(random, cards);
            }
        }
        return cards;
    }


    public static Builder builder()
    {
        return new CompositeShufflingStrategy.Builder();
    }


    public static class Builder
    {
        private List<ShufflingRepeat> strategies = new ArrayList<>();


        public Builder include(ShufflingStrategy shufflingStrategy)
        {
            strategies.add(new ShufflingRepeat(shufflingStrategy, 1, 1));
            return this;
        }


        public Builder repeat(int minTimes, int maxTimes, ShufflingStrategy shufflingStrategy)
        {
            strategies.add(new ShufflingRepeat(shufflingStrategy, minTimes, maxTimes));
            return this;
        }


        public CompositeShufflingStrategy build()
        {
            return new CompositeShufflingStrategy(
                Collections.unmodifiableList(
                    strategies
                )
            );
        }


        public Builder split()
        {
            return this.include(SplitShuffle.DEFAULT);
        }
    }


    @Override
    public String toString()
    {
        return super.toString() + ":\n  "
            + describe()
            ;
    }


    public String describe()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strategies.size(); i++)
        {
            if (i > 0)
            {
                sb.append(", then ");
            }
            ShufflingRepeat repeat = strategies.get(i);
            sb.append(repeat.describe());
        }
        sb.append("\n");
        return sb.toString();
    }
}
