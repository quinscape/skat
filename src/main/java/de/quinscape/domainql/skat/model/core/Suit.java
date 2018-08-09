package de.quinscape.domainql.skat.model.core;

import de.quinscape.domainql.skat.util.Cards;

public enum Suit
{
    CLUBS(12, '\u2663'),
    SPADES(11, '\u2660'),
    HEARTS(10, '\u2665'),
    DIAMONDS(9, '\u2666');

    private final int multiplier;

    private final String name;

    private final char unicode;


    Suit(int multiplier, char unicode)
    {
        this.unicode = unicode;
        this.name = name().substring(0, 1) + name().substring(1).toLowerCase();
        this.multiplier = multiplier;
    }


    public static Suit valueOf(GameType gameType)
    {
        if (gameType == null)
        {
            return null;
        }

        switch (gameType)
        {
            case SUIT_CLUBS:
                return CLUBS;

            case SUIT_SPADES:
                return SPADES;

            case SUIT_HEARTS:
                return HEARTS;

            case SUIT_DIAMONDS:
                return DIAMONDS;
        }
        return null;
    }


    /**
     * Returns the capitalized name of the suit.
     *
     * @return capitalized name (e.g. "Clubs")
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns the black unicode symbol for the suit.
     *
     * @return unicode character
     */
    public char getUnicode()
    {
        return unicode;
    }


    /**
     * Returns the suit multiplier value.
     *
     * @return multiplier
     */
    public int getMultiplier()
    {
        return multiplier;
    }


    public static Suit forCard(int card)
    {
        card = Cards.normalized(card) >> 3;

        switch (card)
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
}
