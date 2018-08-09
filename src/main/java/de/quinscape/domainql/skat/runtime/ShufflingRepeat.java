package de.quinscape.domainql.skat.runtime;

import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;

public final class ShufflingRepeat
{
    private final ShufflingStrategy shufflingStrategy;

    private final int minTimes;

    private final int maxTimes;


    public ShufflingRepeat(ShufflingStrategy shufflingStrategy, int minTimes, int maxTimes)
    {
        if (minTimes < 0)
        {
            throw new IllegalArgumentException("minTimes must be 0 or greater");
        }
        if (maxTimes <= 0)
        {
            throw new IllegalArgumentException("maxTimes must be greater than 0");
        }
        if (minTimes > maxTimes)
        {
            throw new IllegalArgumentException("minTimes must be equal to or smaller than maxTimes");
        }

        if (shufflingStrategy == null)
        {
            throw new IllegalArgumentException("shufflingStrategy can't be null");
        }

        this.shufflingStrategy = shufflingStrategy;
        this.minTimes = minTimes;
        this.maxTimes = maxTimes;
    }


    public ShufflingStrategy getShufflingStrategy()
    {
        return shufflingStrategy;
    }


    public int getMinTimes()
    {
        return minTimes;
    }


    public int getMaxTimes()
    {
        return maxTimes;
    }


    @Override
    public String toString()
    {
        if (minTimes != maxTimes)
        {
            return this.getClass().getSimpleName() + ": " + minTimes + " times " + shufflingStrategy;
        }
        else
        {
            return this.getClass().getSimpleName() + ": " + minTimes + " to " + maxTimes + " times " + shufflingStrategy;
        }
    }
}
