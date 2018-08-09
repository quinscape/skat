package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.model.channel.Channel;
import de.quinscape.domainql.skat.model.core.SkatHand;
import de.quinscape.spring.jsview.util.JSONUtil;

/**
 * A functional equivalent of the Javascript client game state for testing purposes.
 */
public class ClientState
{
    private final Channel channel;
    private final SkatHand hand;


    public ClientState(Channel channel, SkatHand hand)
    {
        this.channel = channel;
        this.hand = hand;
    }


    public Channel getChannel()
    {
        return channel;
    }


    public SkatHand getHand()
    {
        return hand;
    }


    @Override
    public String toString()
    {
        return super.toString() + ":\n" + JSONUtil.formatJSON(JSONUtil.DEFAULT_GENERATOR.forValue(this));
    }
}
