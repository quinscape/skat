package de.fforw.skat.model;

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


    public static Suit forCard(int card)
    {
        card = (Math.abs(card)-1) >> 3;

        switch(card)
        {
            case 0:
                return DIAMONDS;
            case 1:
                return HEARTS;
            case 2:
                return SPADES;
            case 3:
                return CLUBS;
            default:
                throw new IllegalStateException("Unhandled value: " + card);
        }
    }


    public int getValue()
    {
        return value;
    }
}
