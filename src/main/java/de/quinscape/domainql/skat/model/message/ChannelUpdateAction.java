package de.quinscape.domainql.skat.model.message;

import de.quinscape.domainql.skat.model.core.SkatHand;
import de.quinscape.domainql.skat.model.channel.Channel;

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
        return PushActions.PUSH_CHANNEL_UPDATE;
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
