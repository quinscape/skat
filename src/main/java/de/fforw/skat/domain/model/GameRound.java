package de.fforw.skat.domain.model;

import org.svenson.JSONProperty;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Current state of a game, is signed after it ends.
 */
public class GameRound
{
    private final static List<Integer> INITIAL_DECK = new ArrayList<>(32);
    static
    {
        for (int i=0; i < 32 ; i++)
        {
            INITIAL_DECK.add(i);
        }
    }

    /**
     * 32 Integers describing the initial stack. (netto 118 bits)
     */
    private List<Integer> initialStack;
    private List<String> seating;
    private List<Integer> biddingResult;
    private List<Multiplier> multipliers;

    private String signature;

    public List<Integer> getInitialStack()
    {
        return initialStack;
    }


    public void setInitialStack(List<Integer> initialStack)
    {
        this.initialStack = initialStack;
    }


    public List<String> getSeating()
    {
        return seating;
    }


    public void setSeating(List<String> seating)
    {
        this.seating = seating;
    }


    public List<Integer> getBiddingResult()
    {
        return biddingResult;
    }


    public void setBiddingResult(List<Integer> biddingResult)
    {
        this.biddingResult = biddingResult;
    }


    @PostConstruct
    public void validate()
    {
        if (initialStack == null)
        {
            throw new GameValidationException("No initial stack");
        }

        final int stackSize = initialStack.size();

        if (stackSize != 32)
        {
            throw new GameValidationException("Initial stack must be 32 cards long");
        }

        for (int i = 0; i < stackSize; i++)
        {
            for (int j = stackSize - 1; j > i; j++)
            {
                if (initialStack.get(i).equals(initialStack.get(j)))
                {
                    throw new GameValidationException("Card #" + i + " occurs more than once");
                }
            }
        }

        if (seating == null || seating.size() != 3 && seating.size() != 4)
        {
            throw new GameValidationException("Game must have 3 or 4 players: is " + (seating != null ? seating.size() : "null"));
        }

        if (!seating.stream().allMatch(Objects::nonNull))
        {
            throw new GameValidationException("seating cannot contain null values");
        }

        // if we're not playing HAND
        if (multipliers.stream().noneMatch( m -> m == Multiplier.HAND))
        {
            multipliers.forEach( m -> {
                if (m.isOnlyWithHand())
                {
                    throw new GameValidationException(m + " is only valid in combination with HAND");
                }
            } );
        }

        if (biddingResult == null || biddingResult.size() != 3)
        {
            throw new GameValidationException("bidding result must have 3 maximum bids");
        }

        if (!biddingResult.stream().allMatch(Objects::nonNull))
        {
            throw new GameValidationException("biddingResult cannot contain null values");
        }
    }

    public static GameRound shuffleDeck(Random random)
    {
        final ArrayList<Integer> initialStack = new ArrayList<>(INITIAL_DECK);
        Collections.shuffle(initialStack, random);
        final GameRound game = new GameRound();
        game.setInitialStack(initialStack);
        return game;
    }

    @JSONProperty(value = "_sig", priority = -100)
    public String getSignature()
    {
        return signature;
    }


    public void setSignature(String signature)
    {
        this.signature = signature;
    }


    public List<Multiplier> getMultipliers()
    {
        return multipliers;
    }


    public void setMultipliers(List<Multiplier> multipliers)
    {
        this.multipliers = multipliers;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "initialStack = " + initialStack
            + ", seating = " + seating
            + ", biddingResult = " + biddingResult
            + ", multipliers = '" + multipliers + '\''
            + ", signature = '" + signature + '\''
            ;
    }
}


