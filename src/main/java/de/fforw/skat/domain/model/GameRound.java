package de.fforw.skat.domain.model;

import de.fforw.skat.runtime.HandFetcher;
import de.fforw.skat.runtime.game.InitialStackFetcher;
import de.quinscape.domainql.annotation.GraphQLFetcher;
import de.quinscape.domainql.annotation.GraphQLField;
import org.svenson.JSONProperty;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Current state of a game, is signed after it ends.
 */
public class GameRound
{
    private final static List<Integer> INITIAL_DECK = Arrays.asList(
         1, 2, 3, 4, 5, 6, 7, 8,
         9,10,11,12,13,14,15,16,
        17,18,19,20,21,22,23,24,
        25,26,27,28,29,30,31,32
    );

    /**
     * 32 Integers describing the initial stack. (netto 118 bits)
     */
    private List<Integer> initialStack;
    private List<String> seating;
    private List<Integer> biddingResult;
    private List<Multiplier> multipliers;

    private String signature;

    private GamePhase phase = GamePhase.BIDDING;

    private int currentDealer;

    @GraphQLFetcher(InitialStackFetcher.class)
    public List<Integer> getInitialStack()
    {
        return initialStack;
    }


    public GamePhase getPhase()
    {
        return phase;
    }


    public void setPhase(GamePhase phase)
    {
        this.phase = phase;
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
            final int cardA = initialStack.get(i);
            if (cardA < 1 || cardA > 32)
            {
                throw new GameValidationException("Invalid card value #" + i + ": " + i);
            }

            for (int j = stackSize - 1; j > i; j++)
            {
                final Integer cardB = initialStack.get(j);
                if (cardA == cardB)
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

    @GraphQLFetcher(HandFetcher.class)
    public List<Integer> getHand()
    {
        return null;
    }


    @GraphQLFetcher(HandFetcher.class)
    public Integer getCurrentPosition()
    {
        return null;
    }

    public static GameRound shuffleDeck(Random random)
    {
        final ArrayList<Integer> newStack = new ArrayList<>(INITIAL_DECK);

        flipSigns(newStack, random);

        Collections.shuffle(newStack, random);
        final GameRound game = new GameRound();
        game.setInitialStack(newStack);
        return game;
    }


    private static void flipSigns(ArrayList<Integer> newStack, Random random)
    {
        for (int i = 0; i < newStack.size(); i++)
        {
            if (random.nextBoolean())
            {
                newStack.set(i, -newStack.get(i));
            }
        }
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

    public void nextDealer()
    {
        currentDealer++;
        if (currentDealer == seating.size())
        {
            currentDealer = 0;
        }
    }

    public int getCurrentDealer()
    {
        return currentDealer;
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


