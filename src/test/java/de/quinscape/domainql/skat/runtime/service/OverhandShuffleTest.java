package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.runtime.game.OverhandShuffle;
import de.quinscape.domainql.skat.util.Cards;
import de.quinscape.domainql.skat.TestCards;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class OverhandShuffleTest
{
    private final static Logger log = LoggerFactory.getLogger(OverhandShuffleTest.class);

    private Random random = new SecureRandom();


    @Test
    public void testShuffle()
    {
        final OverhandShuffle shuffle = new OverhandShuffle();
        final List<Integer> deck = shuffle.shuffle(random, TestCards.SORTED_DECK);

        assertThat(Cards.isComplete(deck), is(true));

        final int count = (int) getDeltas(deck).stream().filter(n -> n == -1).count();
        assertThat(count, is(greaterThan(6)));
    }

    private List<Integer> getDeltas(List<Integer> deck)
    {
        List<Integer> list = new ArrayList<>(deck.size());
        int last = 0;
        for (Integer card : deck)
        {
            int delta = last - card;
            last = card;
            list.add(delta);
        }
        return list;
    }
}
