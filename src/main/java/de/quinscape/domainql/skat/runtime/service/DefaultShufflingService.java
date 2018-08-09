package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class DefaultShufflingService
    implements ShufflingService
{
    private final static Logger log = LoggerFactory.getLogger(DefaultShufflingService.class);


    private final Map<String, ? extends ShufflingStrategy> strategies;

    private final String defaultStrategy;


    public <T> DefaultShufflingService(Map<String, ? extends ShufflingStrategy> strategies, String defaultStrategy)
    {
        this.strategies = strategies;
        this.defaultStrategy = defaultStrategy;

        log.info("Created shuffling service with strategies = {}, default = {}", strategies, defaultStrategy);
    }


    public String getDefaultStrategy()
    {
        return defaultStrategy;
    }


    @Override
    public Set<String> getStrategyNames()
    {
        return strategies.keySet();
    }


    @Override
    public ShufflingStrategy getStrategy(String strategyName)
    {
        if (strategyName == null)
        {
            strategyName = defaultStrategy;
        }
        final ShufflingStrategy strategy = strategies.get(strategyName);

        if (strategy == null)
        {
            throw new IllegalStateException("Strategy '" + strategyName + "' not found");
        }

        return strategy;
    }
}
