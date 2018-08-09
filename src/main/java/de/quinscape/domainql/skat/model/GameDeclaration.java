package de.quinscape.domainql.skat.model;

import de.quinscape.domainql.skat.model.core.Announcement;
import de.quinscape.domainql.skat.model.core.GameType;
import de.quinscape.domainql.skat.model.core.Suit;
import de.quinscape.domainql.skat.util.Cards;

import java.util.List;

public class GameDeclaration
{
    private int gameType;

    private int announcement;

    private boolean hand;

    private boolean ouvert;


    public GameDeclaration(
    )
    {
        this(GameType.NULL.ordinal(), -1, null, false, false);
    }

    public GameDeclaration(
        int gameType, int announcement, Suit suit, boolean hand, boolean ouvert
    )
    {
        this.gameType = gameType;
        this.announcement = announcement;
        this.hand = hand;
        this.ouvert = ouvert;
    }


    public int getGameType()
    {
        return gameType;
    }


    public void setGameType(int gameType)
    {
        this.gameType = gameType;
    }


    public int getAnnouncement()
    {
        return announcement;
    }


    public void setAnnouncement(int announcement)
    {
        this.announcement = announcement;
    }


    public boolean isHand()
    {
        return hand;
    }


    public void setHand(boolean hand)
    {
        this.hand = hand;
    }


    public boolean isOuvert()
    {
        return ouvert;
    }


    public void setOuvert(boolean ouvert)
    {
        this.ouvert = ouvert;
    }



    private int getJackMultiplier(List<Integer> filteredCards)
    {
        int suit = 0;

        boolean firstJack = Cards.isJackIn(filteredCards, 0);

        int count = firstJack ? 0 : 4;
        for (int i=1; i < 4 ; i++)
        {
            boolean jack = Cards.isJackIn(filteredCards, i);

            if (jack == firstJack)
            {
                count++;
            }
            else
            {
                break;
            }

        }
        // no "without 4"
        if (count == 7)
        {
            return 0;
        }
        return (count & 3) + 1;
    }



    private final static int[] NULL_GAME_VALUES = new int[]{
        23,
        35,
        46,
        59
    };

    public int getGameValue(List<Integer> filteredCards)
    {
        int jackMultiplier = getJackMultiplier(filteredCards);
        int handMultiplier = !hand ? 0 : announcement < 0 ? 1 : announcement + 1;

        if (gameType == GameType.NULL.ordinal())
        {
            return NULL_GAME_VALUES[(ouvert ? 2 : 0) + (hand ? 1 : 0)];
        }
        else if (jackMultiplier > 0 && gameType == GameType.GRAND.ordinal())
        {
            return 24 * ( 1 + jackMultiplier + handMultiplier );
        }
        else if (jackMultiplier > 0  && gameType <= 3)
        {
            return Suit.values()[gameType].getMultiplier() * ( 1 + jackMultiplier + handMultiplier );
        }
        return -1;
    }

    public String getDescription(List<Integer> cards)
    {
        final String jackDescription = getJackDescription(cards);
        final String handDescription = (hand ? Announcement.description(announcement) : "");

        switch (GameType.valueOf(gameType))
        {
            case SUIT_CLUBS:
            case SUIT_SPADES:
            case SUIT_HEARTS:
            case SUIT_DIAMONDS:
                if (jackDescription != null)
                {
                    return Suit.values()[gameType].getName()+ handDescription;
                }
                break;
            case NULL:
                return "Null" + (hand ? " Hand" : "") + (ouvert ? " Ouvert" : "");

            case GRAND:
                if (jackDescription != null)
                {
                    return "Grand" + handDescription;
                }
                break;
            case RAMSCH:
                return "Ramsch";
        }

        return null;
    }


    private String getJackDescription(List<Integer> cards)
    {
        final int jackMultiplier = getJackMultiplier(cards);

        if (jackMultiplier == 0)
        {
            return null;
        }

        final int count = jackMultiplier & 3;

        return ((jackMultiplier & 4) == 0 ? ", with " : ", without " )+ count;
    }


    public boolean isValid()
    {
        return gameType >= 0 && gameType < GameType.values().length && announcement < Announcement.values().length;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "gameType = " + gameType
            + ", announcement = " + announcement
            + ", hand = " + hand
            + ", ouvert = " + ouvert
            ;
    }
}
