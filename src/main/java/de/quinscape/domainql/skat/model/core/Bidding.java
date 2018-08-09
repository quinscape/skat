package de.quinscape.domainql.skat.model.core;

import de.quinscape.domainql.skat.runtime.service.GameLogicException;
import de.quinscape.domainql.skat.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class Bidding
{
    private final static Logger log = LoggerFactory.getLogger(Bidding.class);

    private final boolean allowContinue;

    private List<Bid> bids = new ArrayList<>();

    private int nextValue = 18;
    private int currentBid = -1;
    private boolean isBidTurn = true;

    private Position bidder;
    private Position responder;
    private Position declarer;

    private boolean skatPickedUp = false;

    public Bidding(boolean allowContinue)
    {
        this.allowContinue = allowContinue;
        this.bidder = Position.BID;
        this.responder = Position.RESPOND;
    }
    
    public Bidding(Bidding bidding)
    {
        bids = new ArrayList<>(bidding.bids);
        allowContinue = bidding.allowContinue;
        nextValue = bidding.nextValue;
        currentBid = bidding.currentBid;
        isBidTurn = bidding.isBidTurn;
        bidder = bidding.bidder;
        responder = bidding.responder;
        declarer = bidding.declarer;
    }


    public List<Bid> getBids()
    {
        return bids;
    }

    public int getNextValue()
    {
        return nextValue;
    }

    public Bidding accepted(Position position, int value)
    {
        log.debug("accepted: {} accepts {}", position, value);

        List<Bid> newBids = new ArrayList<>(this.bids);

        final int targetValue = position == Position.BID ? nextValue : currentBid;


        newBids.add(0, new Bid(value, position));

        final Bidding newBidding = new Bidding(this);
        newBidding.bids = newBids;
        newBidding.currentBid = value;
        newBidding.nextValue = Util.getNextGameValue(value);

        if (position == responder)
        {
            if (isBidTurn)
            {
                throw new GameLogicException("Position " + position + " has no current bid right.");
            }
            else
            {
                newBidding.isBidTurn = true;
            }
        }
        else if (position == bidder)
        {
            // if the responder is null both bid and respond both passed and deal can either pass to ramsch/cancel or
            // bid 18 and be declarer.
            if (responder == null)
            {
                newBidding.declarer = bidder;
                newBidding.bidder = null;
                newBidding.responder = null;
                return newBidding;
            }


            if (isBidTurn)
            {
                newBidding.isBidTurn = false;
            }
        }
        else
        {
            throw new GameLogicException(position + " has no bid right.");
        }

        if (value < targetValue)
        {
            throw new GameLogicException("New bid "+ value + " must be higher than  " + targetValue );
        }

        if ( position == responder && value > targetValue)
        {
            throw new GameLogicException("Responder can't overbid.");
        }

        return newBidding;
    }

    public Bidding pass(Position passer)
    {
        log.debug("accepted: {} passes", passer);

        final Bidding newBidding = new Bidding(this);

        switch (bidder)
        {
            case BID:

                final Position remain = passer == bidder ? responder : bidder;

                if (responder !=  Position.RESPOND)
                {
                    newBidding.declarer = remain;
                    newBidding.bidder = null;
                    newBidding.responder = null;
                    return newBidding;
                }

                newBidding.bidder = remain;
                newBidding.responder =  allowContinue ? Position.CONTINUE : Position.DEAL;
                newBidding.isBidTurn = currentBid == -1;
                break;

            case RESPOND:
                if (currentBid == -1)
                {
                    if (passer == bidder)
                    {
                        newBidding.bidder = this.responder;
                        newBidding.responder = null;
                    }
                    else
                    {
                        throw new IllegalStateException( responder + " has no bid right");
                    }
                }
                else
                {
                    if (passer == bidder)
                    {
                        newBidding.declarer = this.responder;
                    }
                    else
                    {
                        newBidding.declarer = this.bidder;
                    }
                    newBidding.bidder = null;
                    newBidding.responder = null;
                }
                break;

            case DEAL:
            case CONTINUE:
                newBidding.declarer = Position.CONTINUE;
                newBidding.bidder = null;
                newBidding.responder = null;
                break;
        }
        return newBidding;
    }

    public Position getBidder()
    {
        return bidder;
    }

    public Position getResponder()
    {
        return responder;
    }

    public Position getDeclarer()
    {
        return declarer;
    }


    public int getCurrentBid()
    {
        return currentBid;
    }


    public boolean isBidTurn()
    {
        return isBidTurn;
    }


    public boolean isSkatPickedUp()
    {
        return skatPickedUp;
    }


    public void setSkatPickedUp(boolean skatPickedUp)
    {
        this.skatPickedUp = skatPickedUp;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "bidder = " + bidder
            + ", responder = " + responder
            + ", currentBid = " + currentBid
            + ", nextValue = " + nextValue
            + ", isBidTurn = " + isBidTurn
            + ", declarer = " + declarer
            + ", skatPickedUp = " + skatPickedUp 
            + ", bids = " + bids
            ;
    }
}
