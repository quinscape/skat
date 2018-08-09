package de.quinscape.domainql.skat.runtime;

import de.quinscape.domainql.skat.model.core.Position;
import org.junit.Test;

import static de.quinscape.domainql.skat.runtime.game.HandFetcher.getCurrentPosition;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class HandFetcherTest
{

    @Test
    public void testGetCurrentPosition()
    {
        assertThat(getCurrentPosition(0,0,3), is(Position.DEAL));
        assertThat(getCurrentPosition(1,0,3), is(Position.RESPOND));
        assertThat(getCurrentPosition(2,0,3), is(Position.BID));

        assertThat(getCurrentPosition(0,1,3), is(Position.BID));
        assertThat(getCurrentPosition(1,1,3), is(Position.DEAL));
        assertThat(getCurrentPosition(2,1,3), is(Position.RESPOND));
        
        assertThat(getCurrentPosition(0,2,3), is(Position.RESPOND));
        assertThat(getCurrentPosition(1,2,3), is(Position.BID));
        assertThat(getCurrentPosition(2,2,3), is(Position.DEAL));

        assertThat(getCurrentPosition(3,0,4), is(Position.CONTINUE));
        assertThat(getCurrentPosition(3,1,4), is(Position.BID));
        assertThat(getCurrentPosition(3,2,4), is(Position.RESPOND));
        assertThat(getCurrentPosition(3,3,4), is(Position.DEAL));
    }
}
