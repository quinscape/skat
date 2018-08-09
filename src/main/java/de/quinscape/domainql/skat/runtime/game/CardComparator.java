package de.quinscape.domainql.skat.runtime.game;

import de.quinscape.domainql.skat.model.core.GameType;
import de.quinscape.domainql.skat.model.core.Suit;
import de.quinscape.domainql.skat.util.Cards;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class CardComparator
    implements Comparator<Integer>
{
    private final static Logger log = LoggerFactory.getLogger(CardComparator.class);

    private final GameType gameType;

    public CardComparator()
    {
        this(null);
    }

    public CardComparator(int gameType)
    {
        this(GameType.valueOf(gameType));
    }
    public CardComparator(GameType gameType)
    {
        this.gameType = gameType;
    }


    // we have to map from the normal indexing for card values to the null ordering
    //  J  A 10  K  Q  9  8  7
    //  A  K  Q  J 10  9  8  7
    //-------------------------
    //  7  6  5  4  3  2  1  0
    public final static int[] NULL_RANK = new int[]{ 0, 1, 2, 5, 6, 3, 7, 4};

    @Override
    public int compare(final Integer a, final Integer b)
    {

        int cardA = Cards.normalized(a);
        int cardB = Cards.normalized(b);

        final int bonusA;
        final int bonusB;
        if (gameType == GameType.NULL)
        {
            cardA = (cardA & 0x18) + NULL_RANK[cardA & 7];
            cardB = (cardB & 0x18) + NULL_RANK[cardB & 7];
            bonusA = 0;
            bonusB = 0;
        }
        else
        {
            Suit trump = Suit.valueOf(gameType);

            bonusA = (cardA & 7) == 7 ? 256 : ( trump != null && Suit.forCard(cardA + 1) == trump) ? 128 : 0;
            bonusB = (cardB & 7) == 7 ? 256 : ( trump != null && Suit.forCard(cardB + 1) == trump) ? 128 : 0;
        }

        final int result = (cardB + bonusB) - (cardA + bonusA);

        log.debug("cardA = {} (bonus = {}), cardB = {} (bonus = {}) => {}", a, bonusA, b, bonusB, result);
        return result;
    }
}
