package de.fforw.skat.domain.model.channel;

import de.quinscape.domainql.annotation.GraphQLObject;

import java.util.List;

public class ChannelListing
{
    private final String id;

    private final List<String> users;

    private final boolean gameInProgress;


    public ChannelListing(String id, List<String> users, boolean gameInProgress)
    {

        this.id = id;
        this.users = users;
        this.gameInProgress = gameInProgress;
    }


    public String getId()
    {
        return id;
    }


    public List<String> getUsers()
    {
        return users;
    }


    public boolean isGameInProgress()
    {
        return gameInProgress;
    }
}
