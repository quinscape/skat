package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.runtime.TestShuffle;
import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;

import java.util.Collections;
import java.util.Set;

public class TestShufflingService
    implements ShufflingService
{
    @Override
    public Set<String> getStrategyNames()
    {
        return Collections.singleton("testShuffle");
    }


    @Override
    public ShufflingStrategy getStrategy(String strategyName)
    {
        return new TestShuffle();
    }
}
