package de.quinscape.domainql.skat.runtime;

import de.quinscape.domainql.skat.model.core.GameOptions;
import de.quinscape.domainql.skat.model.core.GamePhase;
import de.quinscape.domainql.skat.model.core.GameRound;
import de.quinscape.domainql.skat.model.core.Position;
import de.quinscape.domainql.skat.model.core.SkatHand;
import de.quinscape.domainql.skat.model.user.GameUser;
import de.quinscape.domainql.skat.model.user.GameUserType;
import de.quinscape.domainql.skat.runtime.game.OverhandShuffle;
import de.quinscape.domainql.skat.runtime.game.RiffleShuffle;
import de.quinscape.domainql.skat.runtime.game.SplitShuffle;
import de.quinscape.domainql.skat.util.Cards;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static de.quinscape.domainql.skat.runtime.game.HandFetcher.*;


@Ignore
public class NaturalShuffleDistributionTest
{
    private final static int REPEAT = 1000000;

    private final static Logger log = LoggerFactory.getLogger(NaturalShuffleDistributionTest.class);

    private static final CompositeShufflingStrategy shufflingStrategy = CompositeShufflingStrategy.builder()
        .repeat(2, 3, new OverhandShuffle())
        .split()
        .repeat(3, 4, new RiffleShuffle())
        .repeat(0, 1, SplitShuffle.DEFAULT)
        .build();


    @Test
    public void testDistribution()
    {
        Random random = new SecureRandom();

        List<GameUser> users = Arrays.asList(
            new GameUser("1253ce43-0d39-473a-9269-74a15e7a7dea", "user-a", GameUserType.TEST_USER, "aaa", true),
            new GameUser("003b7e9a-8019-4738-8854-8caf1385af13", "user-b", GameUserType.TEST_USER, "bbb", true),
            new GameUser("abaf9cd4-d2d6-4cb9-b9a5-abb1038d39b7", "user-c", GameUserType.TEST_USER, "ccc", true)
        );

        List<Stats> statsA = new ArrayList<>(REPEAT);
        List<Stats> statsB = new ArrayList<>(REPEAT);
        List<Stats> statsC = new ArrayList<>(REPEAT);

        for (int i = 0; i < REPEAT; i++)
        {
            final List<Integer> initialDeck = new ArrayList<>(Cards.INITIAL_DECK);
            //Collections.shuffle(initialDeck, random);

            final GameRound gameRound = GameRound.shuffleDeck(
                shufflingStrategy,
                random,
                0,
                initialDeck,
                GameOptions.DEFAULT_OPTIONS
            );
            gameRound.setSeating(users);
            gameRound.setPhase(GamePhase.PLAYING);

            statsA.add(new Stats(Objects.requireNonNull(getHand(gameRound, null, Position.DEAL))));
            statsB.add(new Stats(Objects.requireNonNull(getHand(gameRound, null, Position.RESPOND))));
            statsC.add(new Stats(Objects.requireNonNull(getHand(gameRound, null, Position.BID))));
        }

        log.info("After {} shuffles of an initial deck", REPEAT);

        logStats("User A", statsA);
        logStats("User B", statsB);
        logStats("User C", statsC);


    }


    private void logStats(String msg, List<Stats> stats)
    {
        log.info("{}:\n{}", msg, Stats.describe(stats, REPEAT));
    }


    static class Stats
    {
        public final int jacks;

        public final int score;

        public final int longestStrait;


        public Stats(SkatHand hand)
        {

            final List<Integer> cards = hand.getCards();
            final List<Integer> normed = cards.stream().map(Cards::normalized).collect(Collectors.toList());
            jacks = (int) normed.stream()
                .filter(
                    c -> (c & 7) == 7
                )
                .count();

            score = Cards.score(cards);

            List<Strait> straits = new ArrayList<>(4);
            straits.add(new Strait(0));
            straits.add(new Strait(1));
            straits.add(new Strait(2));
            straits.add(new Strait(3));

            normed.forEach(c -> straits.get(c >> 3).count++);

            straits.sort(Comparator.comparingInt(a -> a.count));

            longestStrait = straits.get(3).count;
        }


        public static String describe(List<Stats> stats, int repeat)
        {
            StringBuilder buff = new StringBuilder();

            double jacks = 0;
            double score = 0;
            double longestStrait = 0;


            for (Stats stat : stats)
            {
                jacks += stat.jacks;
                score += stat.score;
                longestStrait += stat.longestStrait;
            }

            buff.append("Average number of jacks: ").append(jacks / repeat).append("\n");
            buff.append("Average card score: ").append(score / repeat).append("\n");
            buff.append("Average longest straight: ").append(longestStrait / repeat).append("\n");


            return buff.toString();
        }


    }

    static class Strait
    {
        public final int suit;

        public int count = 0;


        public Strait(int suit)
        {
            this.suit = suit;
        }
    }

}
