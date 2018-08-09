package de.quinscape.domainql.skat.model.message;

import de.quinscape.domainql.skat.model.channel.LogEntry;

public final class ChatAction
{
    private final LogEntry payload;


    public ChatAction(LogEntry payload)
    {
        this.payload = payload;
    }


    public LogEntry getPayload()
    {
        return payload;
    }

    public String getType()
    {
        return PushActions.PUSH_CHAT_MESSAGE;
    }
}
