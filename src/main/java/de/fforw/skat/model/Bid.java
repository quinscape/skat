package de.fforw.skat.model;

public final class Bid
{
    private final int value;

    private final int position;


    public Bid(int value, int position, int numberOfSeats)
    {
        if (position < 0 || position > numberOfSeats)
        {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        this.value = value;
        this.position = position;
    }


    public int getValue()
    {
        return value;
    }


    public int getPosition()
    {
        return position;
    }
}
