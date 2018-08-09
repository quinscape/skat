package de.quinscape.domainql.skat.model.core;

public enum GameType
{
    SUIT_CLUBS,
    SUIT_SPADES,
    SUIT_HEARTS,
    SUIT_DIAMONDS,
    NULL,
    GRAND,
    RAMSCH;


    public static GameType valueOf(int gameType)
    {
        return GameType.values()[gameType];
    }
}
