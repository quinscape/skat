package de.fforw.skat.domain.model.channel;

import de.fforw.skat.domain.model.GameRound;
import org.svenson.JSONTypeHint;

import java.util.ArrayList;
import java.util.List;

public class Channel
{
    private final String id;

    private List<String> users = new ArrayList<>();

    private List<String> owners = new ArrayList<>();

    private List<ChatMessage> chatMessages = new ArrayList<>();

    private List<GameRound> history = new ArrayList<>();

    private GameRound current;

    private boolean publicGame;


    public Channel()
    {
        this(null);
    }


    public Channel(String id)
    {
        this.id = id;
    }


    public String getId()
    {
        return id;
    }


    public List<String> getUsers()
    {
        return users;
    }


    public void setUsers(List<String> users)
    {
        this.users = users;
    }


    public List<ChatMessage> getChatMessages()
    {
        return chatMessages;
    }


    public void setChatMessages(List<ChatMessage> chatMessages)
    {
        this.chatMessages = chatMessages;
    }


    @JSONTypeHint(GameRound.class)
    public List<GameRound> getHistory()
    {
        return history;
    }


    public void setHistory(List<GameRound> history)
    {
        this.history = history;
    }


    public GameRound getCurrent()
    {
        return current;
    }


    public void setCurrent(GameRound current)
    {
        this.current = current;
    }


    public void setPublic(boolean publicGame)
    {
        this.publicGame = publicGame;
    }


    public boolean isPublic()
    {
        return publicGame;
    }


    public List<String> getOwners()
    {
        return owners;
    }


    public void setOwners(List<String> owners)
    {
        this.owners = owners;
    }


    public ChannelListing getListing()
    {
        return new ChannelListing(id, users, current != null);
    }
}
