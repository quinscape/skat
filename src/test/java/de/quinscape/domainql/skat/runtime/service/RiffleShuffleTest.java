package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.runtime.game.RiffleShuffle;
import de.quinscape.domainql.skat.util.Cards;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class RiffleShuffleTest
{
    private final static Logger log = LoggerFactory.getLogger(RiffleShuffleTest.class);


    @Test
    public void testRiffleShuffle()
    {
        final List<Integer> deck = new RiffleShuffle().shuffle(new Random(0), Cards.INITIAL_DECK);

        log.info("{}", deck);

        assertThat(Cards.isComplete(deck), is(true));

    }
}
