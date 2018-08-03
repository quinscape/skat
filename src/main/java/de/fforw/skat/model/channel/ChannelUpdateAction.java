package de.fforw.skat.model.channel;

import de.fforw.skat.model.SkatHand;

public final class ChannelUpdateAction
{
    private final Channel channel;

    private final SkatHand hand;

    public ChannelUpdateAction(Channel channel, SkatHand hand)
    {
        this.channel = channel;
        this.hand = hand;
    }

    public String getType()
    {
        return "PUSH_CHANNEL_UPDATE";
    }

    public Channel getChannel()
    {
        return channel;
    }


    public SkatHand getHand()
    {
        return hand;
    }
}
