package de.quinscape.domainql.skat.model.core;

public final class Bid
{
    private final int value;

    private final Position position;

    public Bid(int value, Position position)
    {
        if (position == null)
        {
            throw new IllegalArgumentException("position can't be null");
        }

        if (value < 18)
        {
            throw new IllegalArgumentException("Value must be 18 or greater");
        }

        this.value = value;
        this.position = position;
    }


    public int getValue()
    {
        return value;
    }


    public Position getPosition()
    {
        return position;
    }
}
