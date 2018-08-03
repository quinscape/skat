package de.fforw.skat.model;

import de.fforw.skat.SkatGameException;
import de.fforw.skat.runtime.HandFetcher;
import de.fforw.skat.runtime.game.InitialStackFetcher;
import de.quinscape.domainql.annotation.GraphQLFetcher;
import org.svenson.JSONProperty;
import org.svenson.JSONTypeHint;

import javax.annotation.PostConstruct;
import java.time.Instant;
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
        9, 10, 11, 12, 13, 14, 15, 16,
        17, 18, 19, 20, 21, 22, 23, 24,
        25, 26, 27, 28, 29, 30, 31, 32
    );

    /**
     * 32 Integers describing the initial stack.
     */
    private List<Integer> initialStack;

    private List<GameUser> seating = new ArrayList<>();

    private Bidding bidding = new Bidding();

    private List<Multiplier> multipliers = new ArrayList<>();

    private String signature;

    private GamePhase phase = GamePhase.OPEN;

    private GameOptions options = new GameOptions();

    private int currentDealer;

    private String lastUpdated;

    @GraphQLFetcher(InitialStackFetcher.class)
    public List<Integer> getInitialStack()
    {
        return phase == GamePhase.FINISHED ? initialStack : Collections.emptyList();
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


    public List<GameUser> getSeating()
    {
        return seating;
    }


    @JSONTypeHint(GameUser.class)
    public void setSeating(List<GameUser> seating)
    {
        this.seating = seating;
    }


    public Bidding getBidding()
    {
        return bidding;
    }


    @JSONTypeHint(Bidding.class)
    public void setBidding(Bidding bidding)
    {
        this.bidding = bidding;
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
            throw new GameValidationException(
                "Game must have 3 or 4 players: is " + (seating != null ? seating.size() : "null"));
        }

        if (!seating.stream().allMatch(Objects::nonNull))
        {
            throw new GameValidationException("seating cannot contain null values");
        }

        // if we're not playing HAND
        if (multipliers.stream().noneMatch(m -> m == Multiplier.HAND))
        {
            multipliers.forEach(m -> {
                if (m.isOnlyWithHand())
                {
                    throw new GameValidationException(m + " is only valid in combination with HAND");
                }
            });
        }
//
//        if (bidding == null || bidding.size() != 3)
//        {
//            throw new GameValidationException("bidding result must have 3 maximum bids");
//        }
//
//        if (!bidding.stream().allMatch(Objects::nonNull))
//        {
//            throw new GameValidationException("bidding cannot contain null values");
//        }
    }


    @GraphQLFetcher(HandFetcher.class)
    public SkatHand getHand()
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
        game.setLastUpdated(Instant.now().toString());
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
            + ", bidding = " + bidding
            + ", multipliers = '" + multipliers + '\''
            + ", signature = '" + signature + '\''
            ;
    }


    public GameOptions getOptions()
    {
        return options;
    }


    @JSONTypeHint(GameOptions.class)
    public void setOptions(GameOptions options)
    {
        if (phase != GamePhase.OPEN)
        {
            throw new SkatGameException("Cannot change options of running round: phase = " + phase);
        }
        this.options = options;
    }


    public int getNumberOfSeats()
    {
        return options.isAllowContinue() ? 4 : 3;
    }


    @JSONProperty(ignore = true)
    public List<Integer> getInitialStackInternal()
    {
        return initialStack;
    }


    public String getLastUpdated()
    {
        return lastUpdated;
    }


    public void setLastUpdated(String lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }
    
}


