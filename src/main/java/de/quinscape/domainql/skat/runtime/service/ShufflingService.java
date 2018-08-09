package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;

import java.util.Set;

public interface ShufflingService
{
    Set<String> getStrategyNames();
    
    ShufflingStrategy getStrategy(String strategyName);
}
