package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.model.channel.Channel;
import de.quinscape.domainql.skat.model.channel.ChannelListing;

import java.util.List;

public interface ChannelRepository
{
    Channel getChannelById(String id);
    void updateChannel(Channel gameRound);

    List<ChannelListing> listPublic();

    void flush();
}
