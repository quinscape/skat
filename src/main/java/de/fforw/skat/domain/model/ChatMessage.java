package de.fforw.skat.domain.model;

import java.sql.Timestamp;

public class ChatMessage
{
    private String user;
    private String message;
    private Timestamp timestamp;

    public String getUser()
    {
        return user;
    }


    public void setUser(String user)
    {
        this.user = user;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage(String message)
    {
        this.message = message;
    }


    public Timestamp getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }
}
