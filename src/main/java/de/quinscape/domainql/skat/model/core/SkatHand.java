package de.quinscape.domainql.skat.model.core;

import de.quinscape.domainql.skat.model.user.GameUser;

import java.util.Collections;
import java.util.List;

public final class SkatHand
{
    private final List<Integer> cards;
    private final List<Integer> skat;

    private final GameUser gameUser;

    private final Position currentPosition;
    private final int declarerScore;
    private final int oppositionScore;


    public SkatHand(
        List<Integer> cards, List<Integer> skat, GameUser gameUser, Position currentPosition, int declarerScore,
        int oppositionScore
    )
    {

        this.cards = Collections.unmodifiableList(cards);
        this.skat = skat;
        this.gameUser = gameUser;
        this.currentPosition = currentPosition;
        this.declarerScore = declarerScore;
        this.oppositionScore = oppositionScore;
    }


    public List<Integer> getCards()
    {
        if (cards == null)
        {
            return Collections.emptyList();
        }
        return cards;
    }

    public GameUser getGameUser()
    {
        return gameUser;
    }


    public Position getCurrentPosition()
    {
        return currentPosition;
    }


    public List<Integer> getSkat()
    {
        return skat;
    }


    public int getDeclarerScore()
    {
        return declarerScore;
    }


    public int getOppositionScore()
    {
        return oppositionScore;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "cards = " + cards
            + "skat = " + skat
            + ", gameUser = " + gameUser
            + ", currentPosition = " + currentPosition
            ;
    }
}
