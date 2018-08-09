package de.quinscape.domainql.skat.model.core;

import de.quinscape.domainql.skat.runtime.service.GameLogicException;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;


public class BiddingTest
{
    @Test
    public void testBidding()
    {
        Bidding bidding = new Bidding(false);
        bidding = bidding.accepted(Position.BID, 18);

        assertThat(bidding.getCurrentBid(), is(18));
        assertThat(bidding.getNextValue(), is(20));

        bidding = bidding.accepted(Position.RESPOND, 18);
        bidding = bidding.accepted(Position.BID, 22);
        bidding = bidding.accepted(Position.RESPOND, 22);
        bidding = bidding.accepted(Position.BID, 23);
        bidding = bidding.pass(Position.RESPOND);

        assertThat(bidding.getBidder(), is(Position.BID));
        assertThat(bidding.getResponder(), is(Position.DEAL));

        bidding = bidding.pass(Position.DEAL);

        assertThat(bidding.getDeclarer(), is(Position.BID));
    }

    @Test
    public void testBidding2()
    {
        Bidding bidding = new Bidding(false);
        bidding = bidding.accepted(Position.BID, 18);
        bidding = bidding.accepted(Position.RESPOND, 18);
        bidding = bidding.accepted(Position.BID, 22);
        bidding = bidding.accepted(Position.RESPOND, 22);
        bidding = bidding.pass(Position.BID);

        assertThat(bidding.getBidder(), is(Position.RESPOND));
        assertThat(bidding.getResponder(), is(Position.DEAL));

        bidding = bidding.pass(Position.DEAL);

        assertThat(bidding.getDeclarer(), is(Position.RESPOND));
    }

    @Test
    public void testBidding3()
    {
        Bidding bidding = new Bidding(false);
        bidding = bidding.pass(Position.BID);
        bidding = bidding.pass(Position.RESPOND);
        bidding = bidding.accepted(Position.DEAL, 18);

        assertThat(bidding.getDeclarer(), is(Position.DEAL));
    }

    @Test
    public void testBiddingWithContinue()
    {
        Bidding bidding = new Bidding(true);
        bidding = bidding.accepted(Position.BID, 18);

        assertThat(bidding.getCurrentBid(), is(18));
        assertThat(bidding.getNextValue(), is(20));

        bidding = bidding.accepted(Position.RESPOND, 18);
        bidding = bidding.accepted(Position.BID, 22);
        bidding = bidding.accepted(Position.RESPOND, 22);
        bidding = bidding.accepted(Position.BID, 23);
        bidding = bidding.pass(Position.RESPOND);

        assertThat(bidding.getBidder(), is(Position.BID));
        assertThat(bidding.getResponder(), is(Position.CONTINUE));

        bidding = bidding.accepted(Position.CONTINUE, 23);
        bidding = bidding.pass(Position.BID);

        assertThat(bidding.getDeclarer(), is(Position.CONTINUE));
    }

    @Test(expected = GameLogicException.class)
    public void testResponderOverBidding()
    {
        Bidding bidding = new Bidding(false);
        bidding = bidding.accepted(Position.BID, 18);
        bidding.accepted(Position.RESPOND, 20);

    }


    @Test(expected = GameLogicException.class)
    public void testWrongTurn()
    {
        Bidding bidding = new Bidding(false);
        bidding = bidding.accepted(Position.RESPOND, 18);
    }

    @Test(expected = GameLogicException.class)
    public void testWrongTurn2()
    {
        Bidding bidding = new Bidding(false);
        bidding = bidding.accepted(Position.DEAL, 18);
    }


}
