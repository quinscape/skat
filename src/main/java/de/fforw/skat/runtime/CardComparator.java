package de.fforw.skat.runtime;

import de.fforw.skat.model.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class CardComparator
    implements Comparator<Integer>
{
    private final static Logger log = LoggerFactory.getLogger(CardComparator.class);

    private final Suit trump;

    public CardComparator()
    {
        this(null);
    }

    public CardComparator(Suit trump)
    {
        this.trump = trump;
    }


    @Override
    public int compare(Integer cardA, Integer cardB)
    {
        cardA = (Math.abs(cardA)-1);
        cardB = (Math.abs(cardB)-1);

        final int bonusA = (cardA & 7) == 7 || ( trump != null && Suit.forCard(cardA + 1) == trump) ? 128 : 0;
        final int bonusB = (cardB & 7) == 7 || ( trump != null && Suit.forCard(cardB + 1) == trump) ? 128 : 0;

        final int result = (cardB + bonusB) - (cardA + bonusA);

        log.debug("cardA = {} (bonus = {}), cardB = {} (bonus = {}) => {}", cardA, bonusA, cardB, bonusB, result);

        return result;
    }
}
