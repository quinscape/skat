package de.fforw.skat.model;

import java.util.Collections;
import java.util.List;

public final class SkatHand
{
    private final List<Integer> cards;

    private final GameUser gameUser;

    private final int currentPosition;


    public SkatHand(List<Integer> cards, GameUser gameUser, int currentPosition)
    {

        this.cards = Collections.unmodifiableList(cards);
        this.gameUser = gameUser;
        this.currentPosition = currentPosition;
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


    public int getCurrentPosition()
    {
        return currentPosition;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "cards = " + cards
            + ", gameUser = " + gameUser
            + ", currentPosition = " + currentPosition
            ;
    }
}
