package de.fforw.skat.runtime.service;

import de.fforw.skat.model.GameUser;
import de.fforw.skat.model.channel.Channel;
import de.fforw.skat.model.channel.ChannelListing;
import de.fforw.skat.runtime.message.ConnectionCloseListener;
import de.fforw.skat.runtime.message.PreparedMessages;
import de.fforw.skat.util.Util;
import de.fforw.skat.ws.SkatClientConnection;
import de.fforw.skat.ws.SkatWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryGameRepository
    implements GameRepository, ConnectionCloseListener

{
    private final static Logger log = LoggerFactory.getLogger(InMemoryGameRepository.class);

    private final Map<String, Channel> channels = Collections.synchronizedMap(new HashMap<>());

    public InMemoryGameRepository()
    {
    }


    @Override
    public Channel getChannelById(String id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("id can't be null");
        }


        log.debug("getGameById {}", id);
        return channels.get(id);
    }

    @Override
    public void updateChannel(Channel channel)
    {
        if (channel == null)
        {
            throw new IllegalArgumentException("channel can't be null");
        }

        log.debug("updateGame {}", channel);
        channels.put(channel.getId(), channel);
    }


    @Override
    public List<ChannelListing> listPublic()
    {
        List<ChannelListing> channelListings = new ArrayList<>();

        for (Channel channel : channels.values())
        {
            if (channel.isPublic())
            {
                channelListings.add(channel.getListing());
            }
        }

        log.debug("listPublic {}", channelListings);
        return channelListings;
    }


    @Override
    public void flush()
    {
        channels.clear();
    }


    @Override
    public void onClose(SkatWebSocketHandler webSocketHandler, SkatClientConnection conn)
    {
        final String connectionId = conn.getConnectionId();
        log.debug("onClose: user with connectionId {} left", connectionId);
        

        for (Channel channel : channels.values())
        {
            PreparedMessages preparedMessages = null;
            synchronized (channel)
            {
                final List<GameUser> users = channel.getUsers();
                final List<GameUser> newUsers = new ArrayList<>(users.size());

                final List<String> removed = deactivateUsers(channel, connectionId, newUsers);
                if (removed.size() > 0)
                {
                    channel.setUsers(newUsers);
                    preparedMessages = channel.prepareUpdate(null, Util.joinWithComma(removed) + " left the channel.");
                }
            } // end lock

            if (preparedMessages != null)
            {
                preparedMessages.sendAll(webSocketHandler);
            }
        }
    }


    private List<String> deactivateUsers(
        Channel channel, String connectionId, List<GameUser> newUsers
    )
    {
        List<String> list = new ArrayList<>();
        for (GameUser user : channel.getUsers())
        {
            if (user != null)
            {
                if (!user.getConnectionId().equals(connectionId))
                {
                    newUsers.add(user);
                }
                else
                {
                    newUsers.add(user.deactivate());

                    final String nameOfDeactivated = user.getName();

                    final List<GameUser> newSeating = channel.getCurrent().getSeating().stream().map(
                        u -> u.getName().equals(nameOfDeactivated) ? u.deactivate() : u).collect(
                        Collectors.toList());

                    channel.getCurrent().setSeating(newSeating);

                    list.add(nameOfDeactivated);
                }
            }
        }
        return list;
    }
}
