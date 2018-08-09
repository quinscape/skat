package de.quinscape.domainql.skat.model.core;

public enum Position
{
    DEAL,
    RESPOND,
    BID,
    CONTINUE;


    public static Position valueOf(int value)
    {
        return Position.values()[value];
    }


    public Position next(boolean allowContinue)
    {
        switch (this)
        {

            case DEAL:
                return RESPOND;

            case RESPOND:
                return BID;

            case BID:
                return allowContinue ? CONTINUE : DEAL;

            case CONTINUE:
                return RESPOND;
        }
        throw new IllegalStateException("Unhandled enum" + this);
    }
}
