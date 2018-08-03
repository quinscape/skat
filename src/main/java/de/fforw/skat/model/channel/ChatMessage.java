package de.fforw.skat.model.channel;

import java.sql.Timestamp;
import java.time.Instant;

public class ChatMessage
{
    private String user;
    private String message;
    private String timestamp;

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


    public String getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

}
