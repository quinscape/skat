package de.fforw.skat.domain.model;

import java.util.Collections;
import java.util.List;

public class SkatHand
{
    private List<Integer> cards;

    private List<String> seating;


    public SkatHand()
    {
        this(null, null);
    }
    
    public SkatHand(List<Integer> cards, List<String> seating)
    {

        this.cards = cards;
        this.seating = seating;
    }


    public List<Integer> getCards()
    {
        if (cards == null)
        {
            return Collections.emptyList();
        }
        return cards;
    }


    public void setCards(List<Integer> cards)
    {
        this.cards = cards;
    }


    public List<String> getSeating()
    {
        if (seating == null)
        {
            return Collections.emptyList();
        }
        return seating;
    }


    public void setSeating(List<String> seating)
    {
        this.seating = seating;
    }
}
