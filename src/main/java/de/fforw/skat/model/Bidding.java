package de.fforw.skat.model;

import de.fforw.skat.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class Bidding
{
    private final static Logger log = LoggerFactory.getLogger(Bidding.class);


    private List<Bid> bids = new ArrayList<>();

    private int nextValue = 18;

    private int bidder = Position.BID.ordinal();
    private int responder = Position.RESPOND.ordinal();
    private int declarer = -1;

    public List<Bid> getBids()
    {
        return bids;
    }

    public int getNextValue()
    {
        return nextValue;
    }

    public Bidding accepted(Position position, int value, int numberOfSeats)
    {
        log.debug("accepted: {} accepts {}, seats = {}", position, value, numberOfSeats );

        List<Bid> newBids = new ArrayList<>(this.bids);

        final int targetValue = position == Position.BID ? nextValue : this.bids.get(0).getValue();

        if (value < targetValue)
        {
            throw new IllegalStateException("New bid "+ value + " must be higher than  " + targetValue );
        }

        if (position == Position.RESPOND && value > targetValue)
        {
            throw new IllegalStateException("Responder can't overbid.");
        }

        if ((newBids.size() == 0 && position == Position.RESPOND) || (newBids.size() > 0 && newBids.get(0).getPosition() == position.ordinal() && position.ordinal() != bidder))
        {
            throw new IllegalStateException("Position " + position + " has no current bid right.");
        }

        newBids.add(0, new Bid(value, position.ordinal(), numberOfSeats));

        final Bidding newBidding = new Bidding();
        newBidding.bids = newBids;
        newBidding.nextValue = Util.getNextGameValue(value);

        return newBidding;
    }

    public Bidding pass(int position, boolean allowContinue)
    {
        log.debug("accepted: {} passes, allowContinue = {}", position, allowContinue );

        final Bidding bidding = new Bidding();
        bidding.bids = getBids();

        Position currentBidder = Position.valueOf(bidder);
        Position passer = Position.valueOf(position);

        switch (currentBidder)
        {
            case BID:
                if (passer == Position.BID)
                {
                    bidding.bidder = this.responder;
                    bidding.responder = allowContinue ? Position.CONTINUE.ordinal() : Position.DEAL.ordinal();
                    bidding.nextValue = bidding.bids.size() > 0 ? bidding.bids.get(0).getValue() : 18;
                }
                else
                {
                    bidding.bidder = this.bidder;
                    bidding.responder = allowContinue ? Position.CONTINUE.ordinal() : Position.DEAL.ordinal();
                    bidding.nextValue = bidding.bids.size() > 0 ? bidding.bids.get(0).getValue() : 18;
                }
                break;

            case RESPOND:
                if (passer == Position.RESPOND)
                {
                    bidding.bidder = -1;
                    bidding.responder = -1;
                    bidding.declarer = this.responder;
                }
                else
                {
                    bidding.bidder = -1;
                    bidding.responder = -1;
                    bidding.declarer = this.bidder;
                }
                break;

            case DEAL:
            case CONTINUE:
                throw new IllegalStateException(currentBidder + " has no bid rights");
        }
        return bidding;
    }

    public boolean isFinished()
    {
        return declarer != -1;
    }

    public int getBidder()
    {
        return bidder;
    }

    public int getResponder()
    {
        return responder;
    }

    public int getDeclarer()
    {
        return declarer;
    }
}
