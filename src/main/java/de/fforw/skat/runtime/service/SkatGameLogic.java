package de.fforw.skat.runtime.service;

import de.fforw.skat.domain.model.channel.Channel;
import de.fforw.skat.domain.model.channel.ChannelListing;
import de.fforw.skat.domain.model.channel.ChannelListings;
import de.fforw.skat.domain.model.GameRound;
import de.fforw.skat.runtime.config.AppAuthentication;
import de.quinscape.domainql.annotation.GraphQLField;
import de.quinscape.domainql.annotation.GraphQLLogic;
import de.quinscape.domainql.annotation.GraphQLMutation;
import de.quinscape.domainql.annotation.GraphQLQuery;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@GraphQLLogic
public class SkatGameLogic
{
    private final static Logger log = LoggerFactory.getLogger(SkatGameLogic.class);

    private final DSLContext dslContext;

    private final GameRepository gameRepository;

    private final Random random;


    @Autowired
    public SkatGameLogic(
        DSLContext dslContext,
        GameRepository gameRepository,
        Random random
    )
    {
        this.dslContext = dslContext;
        this.gameRepository = gameRepository;
        this.random = random;

    }


    @GraphQLQuery
    public Channel skateGame(String id)
    {
        log.debug("skateGame({})", id);

        return gameRepository.getGameById(id);
    }


    @GraphQLQuery
    public ChannelListings currentGameList(
        @GraphQLField(defaultValue = "0")
            int offset,
        @GraphQLField(defaultValue = "20")
            int limit
    )
    {
        log.debug("currentGameList({}, {})", offset, limit);

        final List<ChannelListing> channels = gameRepository.listPublic();
        final ChannelListings channelListings = new ChannelListings();

        final int rowCount = channels.size();

        channelListings.setRowCount(rowCount);

        if (rowCount > limit)
        {
            channelListings.setChannels(channels.subList(offset, offset + limit));
        }
        else
        {
            channelListings.setChannels(channels);
        }

        return channelListings;
    }


    @GraphQLMutation
    public Channel createGame(String secret, String windowId, boolean isPublic)
    {
        log.debug("createGame(secret = {}, windowId = {}, isPublic = {})", secret, windowId, isPublic);

        Channel channel = new Channel(secret);
        channel.setPublic(isPublic);
        final GameRound gameRound = GameRound.shuffleDeck(random);

        final AppAuthentication auth = AppAuthentication.current();

        final String userName = getUserName(secret, windowId, auth);

        gameRound.setSeating(Collections.singletonList(userName));
        channel.setCurrent(gameRound);
        final ArrayList<String> owners = new ArrayList<>();
        owners.add(auth.getLogin());
        channel.setOwners(owners);

        gameRepository.updateGame(channel);

        return channel;
    }


    @GraphQLMutation
    public Channel joinGame(String secret, String windowId)
    {
        log.debug("joinGame(secret = {}, windowId = {})", secret, windowId);

        final AppAuthentication current = AppAuthentication.current();
        Channel game = gameRepository.getGameById(secret);

        if (game == null)
        {
            game = new Channel(secret);
        }


        List<String> users = game.getUsers();
        if (users == null)
        {
            users = new ArrayList<>();
            game.setUsers(users);
        }

        users.add(
            getUserName(secret, windowId)
        );

        gameRepository.updateGame(game);

        return game;
    }


    @GraphQLMutation
    public boolean flushGames()
    {
        gameRepository.flush();

        return true;
    }


    private String getUserName(String secret, String windowId)
    {
        final AppAuthentication current = AppAuthentication.current();
        return getUserName(secret, windowId, current);
    }


    private String getUserName(String secret, String windowId, AppAuthentication auth)
    {
        return auth.getRoles().contains("ROLE_TEST") ?
            secret + ":" + windowId :
            secret;
    }
}
