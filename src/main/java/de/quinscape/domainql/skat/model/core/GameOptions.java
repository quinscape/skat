package de.quinscape.domainql.skat.model.core;

import de.quinscape.domainql.skat.runtime.SkatRuntimeException;

public class GameOptions
    implements Cloneable
{
    // use default as configured in the service
    public static final String DEFAULT_SHUFFLING_STRATEGY_NAME = null;

    public static final GameOptions DEFAULT_OPTIONS = new GameOptions();

    private boolean allowContinue = false;

    private boolean allowRamsch = true;

    private boolean jackStraitsOnly = false;

    // default shuffle
    private String shufflingStrategyName = DEFAULT_SHUFFLING_STRATEGY_NAME;

    public boolean isAllowContinue()
    {
        return allowContinue;
    }


    public void setAllowContinue(boolean allowContinue)
    {
        this.allowContinue = allowContinue;
    }


    public void setAllowRamsch(boolean allowRamsch)
    {
        this.allowRamsch = allowRamsch;
    }


    public boolean isAllowRamsch()
    {
        return allowRamsch;
    }


    public boolean isJackStraitsOnly()
    {
        return jackStraitsOnly;
    }


    public void setJackStraitsOnly(boolean jackStraitsOnly)
    {
        this.jackStraitsOnly = jackStraitsOnly;
    }


    public String getShufflingStrategyName()
    {
        return shufflingStrategyName;
    }


    public void setShufflingStrategyName(String shufflingStrategyName)
    {
        this.shufflingStrategyName = shufflingStrategyName;
    }

    public GameOptions clone()
    {
        try
        {
            return (GameOptions) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new SkatRuntimeException(e);
        }
    }
}
