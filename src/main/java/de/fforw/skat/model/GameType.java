package de.fforw.skat.model;

public enum GameType
{
    STRAIGHT_WITH_1(1),
    STRAIGHT_WITH_2(2),
    STRAIGHT_WITH_3(3),
    STRAIGHT_WITH_4(4),
    STRAIGHT_WITHOUT_1(1),
    STRAIGHT_WITHOUT_2(2),
    STRAIGHT_WITHOUT_3(3),
    NULL(23),
    GRAND(24),
    RAMSCH(0);

    private final int value;


    GameType(int value)
    {

        this.value = value;
    }


    public int getValue()
    {
        return value;
    }
}
