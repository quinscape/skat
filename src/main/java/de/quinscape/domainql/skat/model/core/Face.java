package de.quinscape.domainql.skat.model.core;

import de.quinscape.domainql.skat.util.Cards;

public enum Face
{
    SEVEN("7", 0),
    EIGHT("8", 0),
    NINE("9", 0),
    QUEEN("Queen", 3),
    KING("King", 4),
    TEN("10", 10),
    ACE("Ace", 11),
    JACK("Jack", 2);

    private final String name;

    private final int value;


    Face(String name, int value)
    {
        this.name = name;
        this.value = value;
    }


    public static Face forCard(Integer c)
    {
        return Face.values()[Cards.normalized(c) & 7];
    }


    public String getName()
    {
        return name;
    }


    public int getValue()
    {
        return value;
    }
}
