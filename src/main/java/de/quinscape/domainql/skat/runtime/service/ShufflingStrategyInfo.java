package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;
import org.svenson.JSONable;

public final class ShufflingStrategyInfo
{
    private final String name;

    private final String description;

    private final boolean isDefault;


    public ShufflingStrategyInfo(String name, ShufflingStrategy strategy, boolean isDefault)
    {
        this.name = name;
        this.description = strategy.describe();
        this.isDefault = isDefault;
    }


    public String getName()
    {
        return name;
    }


    public String getDescription()
    {
        return description;
    }


    public boolean isDefault()
    {
        return isDefault;
    }
}
