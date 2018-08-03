package de.fforw.skat.model;

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
}
