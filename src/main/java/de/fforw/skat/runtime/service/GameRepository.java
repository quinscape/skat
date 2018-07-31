package de.fforw.skat.runtime.service;

import de.fforw.skat.domain.model.channel.Channel;
import de.fforw.skat.domain.model.channel.ChannelListing;

import java.util.List;

public interface GameRepository
{
    Channel getGameById(String id);
    void updateGame(Channel gameRound);

    List<ChannelListing> listPublic();

    void flush();
}
