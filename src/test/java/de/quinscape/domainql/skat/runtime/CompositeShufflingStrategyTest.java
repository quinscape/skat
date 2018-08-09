package de.quinscape.domainql.skat.runtime;

import de.quinscape.domainql.skat.TestCards;
import de.quinscape.domainql.skat.runtime.game.OverhandShuffle;
import de.quinscape.domainql.skat.runtime.game.RiffleShuffle;
import de.quinscape.domainql.skat.util.Cards;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class CompositeShufflingStrategyTest
{

    private final static Logger log = LoggerFactory.getLogger(CompositeShufflingStrategyTest.class);

    private Random random = new SecureRandom(); // new Random(0);


    @Test
    public void testCompositing()
    {
        final TestShuffle shuffleA = new TestShuffle();
        final TestShuffle shuffleB = new TestShuffle();
        final CompositeShufflingStrategy strategy = CompositeShufflingStrategy.builder()
            .include(shuffleA)
            .repeat(3, 3, shuffleB)
            .build();

        final List<Integer> result = strategy.shuffle(random, TestCards.SORTED_DECK);

        assertThat(shuffleA.getCalls().size(), is(1));
        assertThat(shuffleB.getCalls().size(), is(3));

        final ShufflingCall callOnA = shuffleA.getCalls().get(0);
        assertThat(callOnA.getCards() == TestCards.SORTED_DECK, is(true));

        final ShufflingCall callOnB = shuffleB.getCalls().get(0);
        final ShufflingCall call2OnB = shuffleB.getCalls().get(1);
        final ShufflingCall call3OnB = shuffleB.getCalls().get(2);
        assertThat(callOnB.getCards() == callOnA.getOutput(), is(true));
        assertThat(call2OnB.getCards() == callOnB.getOutput(), is(true));
        assertThat(call3OnB.getCards() == call2OnB.getOutput(), is(true));
        assertThat(            result == call3OnB.getOutput(), is(true));

        assertThat(result, is(Arrays.asList(
            5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 1, 2, 3, 4
        )));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testWrongRepeat()
    {
        final CompositeShufflingStrategy strategy = CompositeShufflingStrategy.builder()
            .repeat(-1, 1 , new TestShuffle())
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongRepeat2()
    {
        final CompositeShufflingStrategy strategy = CompositeShufflingStrategy.builder()
            .repeat(1, -1, new TestShuffle())
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongRepeat3()
    {
        final CompositeShufflingStrategy strategy = CompositeShufflingStrategy.builder()
            .repeat(2, 1, new TestShuffle())
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStrategy()
    {
        final CompositeShufflingStrategy strategy = CompositeShufflingStrategy.builder()
            .repeat(2, 2, null)
            .build();

    }


    @Test
    public void testNaturalShuffle()
    {
        final CompositeShufflingStrategy strategy = CompositeShufflingStrategy.builder()
            .repeat(2 , 4, new OverhandShuffle())
            .repeat(3 , 5, new RiffleShuffle())
            .split()
            .build();


        log.info(strategy.shuffle(random, Cards.INITIAL_DECK).toString());

    }
}
