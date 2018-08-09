package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.runtime.game.SplitShuffle;
import de.quinscape.domainql.skat.util.Cards;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class SplitShuffleTest
{

    private final static Logger log = LoggerFactory.getLogger(SplitShuffleTest.class);


    @Test
    public void testShuffle()
    {
        final List<Integer> deck = SplitShuffle.DEFAULT.shuffle(new Random(0), Cards.INITIAL_DECK);

        assertThat(deck.size(), is(32));
        assertThat(deck.get(0), is(greaterThanOrEqualTo(12)));
        assertThat(deck.get(0), is(greaterThanOrEqualTo(12)));


        log.info(
            deck.toString()
        );
        assertThat(Cards.isComplete(deck), is(true));
    }
}
