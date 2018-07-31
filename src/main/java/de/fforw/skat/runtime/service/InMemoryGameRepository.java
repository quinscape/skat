package de.fforw.skat.runtime.service;

import de.fforw.skat.domain.model.channel.Channel;
import de.fforw.skat.domain.model.channel.ChannelListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryGameRepository
    implements GameRepository

{
    private final static Logger log = LoggerFactory.getLogger(InMemoryGameRepository.class);

    private final Map<String, Channel> games = Collections.synchronizedMap(new HashMap<>());
    
    @Override
    public Channel getGameById(String id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("id can't be null");
        }


        log.info("getGameById {}", id);
        return games.get(id);
    }

    @Override
    public void updateGame(Channel channel)
    {
        if (channel == null)
        {
            throw new IllegalArgumentException("channel can't be null");
        }

        log.info("updateGame {}", channel);
        games.put(channel.getId(), channel);
    }


    @Override
    public List<ChannelListing> listPublic()
    {
        List<ChannelListing> list = new ArrayList<>();

        for (Channel channel : games.values())
        {
            if (channel.isPublic())
            {
                list.add(channel.getListing());
            }
        }

        return list;
    }


    @Override
    public void flush()
    {
        games.clear();
    }
}
