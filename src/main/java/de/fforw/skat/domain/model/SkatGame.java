package de.fforw.skat.domain.model;

import java.util.List;

public class SkatGame
{
    private final String id;

    private List<String> users;
    private List<ChatMessage> chatMessages;
    private List<GameRound> history;
    private GameRound current;

    public SkatGame(String id)
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
}
