package de.fforw.skat.domain.model;

public enum Suit
{
    CLUBS(12),
    SPADES(11),
    HEARTS(10),
    DIAMONDS(9);

    private final int value;


    Suit(int value)
    {

        this.value = value;
    }


    public int getValue()
    {
        return value;
    }
}
