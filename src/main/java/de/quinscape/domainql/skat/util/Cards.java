package de.quinscape.domainql.skat.util;

import de.quinscape.domainql.skat.model.core.Face;
import de.quinscape.domainql.skat.model.core.GamePhase;
import de.quinscape.domainql.skat.model.core.GameRound;
import de.quinscape.domainql.skat.model.core.GameType;
import de.quinscape.domainql.skat.model.core.Suit;
import de.quinscape.domainql.skat.runtime.game.CardComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Cards
{
    public final static int FACE_DOWN_CARD = 33;

    private final static Logger log = LoggerFactory.getLogger(Cards.class);
    
    public static final int NUMBER_OF_CARDS = 32;

    /**
     * Order of the deck of cards I found in my living room
     */
    public final static List<Integer> INITIAL_DECK = Arrays.asList(
        11, 26, 13, 28, 31, 30, 10, 21,
        2, 6, 1, 32, 5, 14, 15, 17,
        25, 12, 20, 27, 8, 22, 19, 3,
        18, 29, 16, 24, 4, 9, 7, 23
    );

    static
    {
        if (!Cards.isComplete(Cards.INITIAL_DECK))
        {
            throw new IllegalStateException("INITIAL_DECK is not a complete deck!");
        }
    }

    private Cards()
    {
    }

    /**
     * Returns true if the jack of the given suit is in the given list of cards
     * @param cards     cards
     * @param suit      suit (0-3)
     * @return  <code>true</code> if the jack of the given suit exists in the cards
     */
    public static boolean isJackIn(List<Integer> cards, int suit)
    {
        final int jackCard = ((3 - suit) * 8 + 7) + 1;

        final boolean result = isCardIn(cards, jackCard);

        if (log.isDebugEnabled())
        {
            log.info("isJackIn( {}, {} => {}", describe(cards), suit, result);
        }

        return result;
    }


    /**
     * Returns true if the given card is in the given list of cards
     * @param cards     cards
     * @param cards card index
     * @return  <code>true</code> if the jack of the given suit exists in the cards
     */
    public static boolean isCardIn(List<Integer> cards, int card)
    {
        card = Cards.normalized(card);

        for (Integer filteredCard : cards)
        {
            if (Cards.normalized(filteredCard) == card)
            {
                return true;
            }
        }
        return false;
    }


    public static int getWinningCard(List<Integer> trick, GameType gameType)
    {
        log.debug("getWinningCard: game type is {}", gameType);
        final int suit = Cards.suit(trick.get(0));
        log.debug("1st suit = {}", suit);

        if (gameType == GameType.NULL)
        {
            int max = -1;
            int maxCard = -1;

            for (int i = 0; i < 3; i++)
            {
                final int card = trick.get(i);
                final int currentSuit = Cards.suit(card);

                if (currentSuit == suit)
                {
                    final int nullRank = CardComparator.NULL_RANK[normalized(card) & 7];
                    if (log.isDebugEnabled())
                    {
                        log.debug("{} has rank {}", cardDescription(card), nullRank);
                    }
                    if (nullRank > max)
                    {
                        max = nullRank;
                        maxCard = card;
                        if (log.isDebugEnabled())
                        {
                            log.debug("New max: {} / {}", max, cardDescription(maxCard));
                        }
                    }
                }
                else
                {
                    log.debug(cardDescription(card) + " does not follow suit");
                }
            }
            return maxCard;
        }
        else if (gameType == GameType.GRAND || gameType == GameType.RAMSCH)
        {
            int max = -1;
            int maxCard = -1;

            for (int i = 0; i < 3; i++)
            {
                final int card = trick.get(i);
                final int currentSuit = Cards.suit(card);

                final int rank;

                if (Cards.isJack(card))
                {
                    rank = 256 + normalized(card);
                }
                else if (currentSuit == suit)
                {
                    rank = normalized(card);
                }
                else
                {
                    rank = -1;
                }

                if (log.isDebugEnabled())
                {
                    log.debug("{} has rank {}", cardDescription(card), rank);
                }

                if (rank > max)
                {
                    max = rank;
                    maxCard = card;
                    if (log.isDebugEnabled())
                    {
                        log.debug("New max: {} / {}", max, cardDescription(maxCard));
                    }
                }
            }
            return maxCard;
        }
        else if (gameType.ordinal() <= 3)
        {
            int max = -1;
            int maxCard = -1;

            final int trump = gameType.ordinal();
            log.debug("Trump is {}", trump);

            for (int i = 0; i < 3; i++)
            {
                final int card = trick.get(i);
                final int currentSuit = Cards.suit(card);

                final int rank;

                if (Cards.isJack(card))
                {
                    rank = 256 + normalized(card);
                }
                else if (currentSuit == trump)
                {
                    rank = 128 + normalized(card);
                }
                else if (currentSuit == suit)
                {
                    rank =  normalized(card);
                }
                else
                {
                    rank = -1;
                }

                if (log.isDebugEnabled())
                {
                    log.debug("{} has rank {}", cardDescription(card), rank);
                }

                if (rank > max)
                {
                    max = rank;
                    maxCard = card;
                    if (log.isDebugEnabled())
                    {
                        log.debug("New max: {} / {}", max, cardDescription(maxCard));
                    }
                }
            }
            return maxCard;
        }
        else
        {
            throw new IllegalStateException("Unhandled game type " + gameType);
        }

    }


    private static boolean isJack(int card)
    {
        return (normalized(card) & 7 ) == 7;
    }

    private static int suit(int card)
    {
        return 3 - (normalized(card) >> 3);
    }


    /**
     * Flips the card sign positive and substracts one that that the original range (-32 to 32, no 0) collapses
     * to 0 to 31.
     *
     * @param value     card value
     *
     * @return  normalized 5-bit card value
     */
    public static int normalized(int value)
    {
        return Math.abs(value) - 1;
    }

    public static String cardDescription(int card)
    {
        card = Cards.normalized(card);
        return Face.values()[card & 7].getName() + " of " + Suit.values()[3 - (card >> 3)].getName();
    }


    public static int score(List<Integer> cards)
    {
        return cards.stream()
            .mapToInt(c -> Face.values()[Cards.normalized(c) & 7].getValue())
            .sum();
    }

    public static boolean isComplete(List<Integer> cards)
    {
        final int numberOfCards = cards.size();
        if (numberOfCards != NUMBER_OF_CARDS)
        {
            log.debug("Cannot be complete, wrong number of cards: {}", numberOfCards);
            return false;
        }

        List<Boolean> isPresent = new ArrayList<>(Collections.nCopies(NUMBER_OF_CARDS, false));
        for (Integer card : cards)
        {
            isPresent.set(Cards.normalized(card), true);
        }

        final boolean allCardsPresent = isPresent.stream().allMatch(o -> (boolean) o);

        if (log.isDebugEnabled() && !allCardsPresent)
        {
            log.debug("isPresent = {}{}", isPresent, " (first 'false' at " + cards.indexOf(false) + ")");
        }

        return allCardsPresent;
    }


    /**
     * Provides a human readable description for the given list of card values.
     *
     * @param cards     list of card values, non-normalized.
     *
     * @return description of cards including card values
     */
    public static String describe(List<Integer> cards)
    {
        StringBuilder buff = new StringBuilder();
        for (Integer card : cards)
        {
            buff.append( Cards.cardDescription(card))
                .append(" (")
                .append(card)
                .append(")\n");
        }
        return  buff.toString();
    }

    /**
     * Collects the played cards of a finished game into a new card stack, randomly placing either the declarer won
     * or the opposition won cards on top.
     *
     * @param random    random generator to use
     * @param current   finished game
     *
     * @return stack of cards
     */
    public static List<Integer> collectCards(Random random, GameRound current)
    {
        if (current.getPhase() != GamePhase.FINISHED)
        {
            throw new IllegalArgumentException("Game round not finished: " + current);
        }

        final boolean declarerFirst = random.nextBoolean();

        final ArrayList<Integer> stack = new ArrayList<>();

        if (declarerFirst)
        {
            stack.addAll(current.getDeclarerWonCards());
            stack.addAll(current.getOppositionWonCards());
        }
        else
        {
            stack.addAll(current.getOppositionWonCards());
            stack.addAll(current.getDeclarerWonCards());
        }

        if (!isComplete(stack))
        {
            throw new IllegalStateException("Collected stack is not complete: " + Cards.describe(stack));
        }

        return stack;
    }
}
