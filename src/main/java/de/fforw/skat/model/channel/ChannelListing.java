package de.fforw.skat.model.channel;

import de.fforw.skat.model.GameUser;

import java.util.List;

public class ChannelListing
{
    private final String id;

    private final List<GameUser> users;

    private final boolean gameInProgress;


    public ChannelListing(String id, List<GameUser> users, boolean gameInProgress)
    {

        this.id = id;
        this.users = users;
        this.gameInProgress = gameInProgress;
    }


    public String getId()
    {
        return id;
    }


    public List<GameUser> getUsers()
    {
        return users;
    }


    public boolean isGameInProgress()
    {
        return gameInProgress;
    }
}
