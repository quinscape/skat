package de.quinscape.domainql.skat.model.core;

import de.quinscape.domainql.skat.SkatGameException;
import de.quinscape.domainql.skat.model.GameDeclaration;
import de.quinscape.domainql.skat.model.user.GameUser;
import de.quinscape.domainql.skat.runtime.GameValidationException;
import de.quinscape.domainql.skat.runtime.game.HandFetcher;
import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;
import de.quinscape.domainql.skat.util.Cards;
import de.quinscape.domainql.annotation.GraphQLFetcher;
import org.svenson.JSONProperty;
import org.svenson.JSONTypeHint;

import javax.annotation.PostConstruct;
import java.time.Instant;
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
    /**
     * 32 Integers describing the initial stack.
     */
    private List<Integer> initialStack;

    private List<GameUser> seating = new ArrayList<>();

    private Bidding bidding;

    private String signature;

    private GamePhase phase = GamePhase.OPEN;

    private GameOptions options = new GameOptions();

    private int currentDealer;

    private Position nextPlayer;

    private String lastUpdated;

    private GameDeclaration gameDeclaration;

    private List<Integer> declarerWonCardsRO;

    private List<Integer> oppositionWonCardsRO;

    private List<Integer> trick = new ArrayList<>();

    private int oppositionScore;

    private int declarerScore;

    private int skatScore;

    public GameRound()
    {
        setDeclarerWonCards(new ArrayList<>());
        setOppositionWonCards(new ArrayList<>());
    }

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

        if (stackSize != Cards.NUMBER_OF_CARDS)
        {
            throw new GameValidationException("Initial stack must be " + Cards.NUMBER_OF_CARDS + " cards long");
        }

        for (int i = 0; i < stackSize; i++)
        {
            final int cardA = initialStack.get(i);
            if (cardA < 1 || cardA > Cards.NUMBER_OF_CARDS)
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

    public static GameRound shuffleDeck(
        ShufflingStrategy shufflingStrategy, Random random, int currentDealer,
        List<Integer> existingStack, GameOptions options
    )
    {

        final List<Integer> shuffled = shufflingStrategy.shuffle(random, new ArrayList<>(existingStack));
        //flipSigns(shuffled, random);

        final GameRound game = new GameRound();
        game.setCurrentDealer(currentDealer);
        game.setInitialStack(shuffled);
        game.setLastUpdated(Instant.now().toString());
        final Bidding bidding = new Bidding(options.isAllowContinue());
        game.setBidding(bidding);
        
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


    public GameDeclaration getGameDeclaration()
    {
        return gameDeclaration;
    }


    public void setGameDeclaration(GameDeclaration gameDeclaration)
    {
        this.gameDeclaration = gameDeclaration;
    }


    public List<Integer> getDeclarerWonCards()
    {
        return phase == GamePhase.FINISHED ? declarerWonCardsRO : Collections.emptyList();
    }

    @JSONProperty(ignore = true)
    public List<Integer> getDeclarerWonCardsInternal()
    {
        return declarerWonCardsRO;
    }


    public void setDeclarerWonCards(final List<Integer> declarerWonCards)
    {
        this.declarerWonCardsRO = Collections.unmodifiableList(declarerWonCards);
        this.declarerScore = Cards.score(declarerWonCards);
        if (declarerWonCards.size() >= 2)
        {
            this.skatScore = Cards.score(declarerWonCards.subList(0, 2));
        }
        else
        {
            this.skatScore = 0;
        }
    }


    public List<Integer> getOppositionWonCards()
    {
        return phase == GamePhase.FINISHED ? oppositionWonCardsRO : Collections.emptyList();
    }


    @JSONProperty(ignore = true)
    public List<Integer> getOppositionWonCardsInternal()
    {
        return oppositionWonCardsRO;
    }


    public void setOppositionWonCards(List<Integer> oppositionWonCards)
    {
        this.oppositionWonCardsRO = Collections.unmodifiableList(oppositionWonCards);
        this.oppositionScore = Cards.score(oppositionWonCards);
    }


    public List<Integer> getTrick()
    {
        return trick;
    }


    public void setTrick(List<Integer> trick)
    {
        this.trick = trick;
    }


    public int getSeat(int n)
    {
        int seat = n % 3;
        if (seat < 0)
        {
            seat += 3;
        }
        return  getNumberOfSeats() == 4 ? 1 + seat : seat;
    }

    public Position getSeatPosition(int n)
    {
        return Position.values()[getSeat(n)];
    }

    public Position getNextPlayer()
    {
        return nextPlayer;
    }


    public void setNextPlayer(Position nextPlayer)
    {
        this.nextPlayer = nextPlayer;
    }


    public void setCurrentDealer(int currentDealer)
    {
        this.currentDealer = currentDealer;
    }


    @JSONProperty(ignore = true)
    public int getDeclarerScoreInternal()
    {
        return declarerScore;
    }


    @JSONProperty(ignore = true)
    public int getOppositionScoreInternal()
    {
        return oppositionScore;
    }


    @JSONProperty(ignore = true)
    public int getSkatScoreInternal()
    {
        return skatScore;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "initialStack = " + initialStack
            + ", seating = " + seating
            + ", bidding = " + bidding
            + ", signature = '" + signature + '\''
            + ", phase = " + phase
            + ", options = " + options
            + ", currentDealer = " + currentDealer
            + ", nextPlayer = " + nextPlayer
            + ", lastUpdated = '" + lastUpdated + '\''
            + ", gameDeclaration = " + gameDeclaration
            + ", declarerWonCardsRO = " + declarerWonCardsRO
            + ", oppositionWonCardsRO = " + oppositionWonCardsRO
            + ", trick = " + trick
            + ", oppositionScore = " + oppositionScore
            + ", declarerScore = " + declarerScore
            + ", skatScore = " + skatScore
            ;
    }
}



