package de.fforw.skat.runtime.service;

import de.fforw.skat.model.channel.Channel;
import de.fforw.skat.model.channel.ChannelListing;

import java.util.List;

public interface GameRepository
{
    Channel getChannelById(String id);
    void updateChannel(Channel gameRound);

    List<ChannelListing> listPublic();

    void flush();
}
