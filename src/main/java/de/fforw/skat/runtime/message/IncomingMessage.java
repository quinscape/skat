package de.fforw.skat.runtime.message;

import org.svenson.JSONParameter;

/**
 * Container for incoming messages.
 *
 */
public final class IncomingMessage
{
    private final String connectionId;
    private final String messageId;
    private final Object payload;
    private final String type;

    public IncomingMessage(
        @JSONParameter("connectionId")
        String connectionId,
        @JSONParameter("messageId")
        String messageId,
        @JSONParameter("payload")
        Object payload,
        @JSONParameter("type")
        String type
    )
    {
        this.type = type != null ? type : GraphQLMessageHandler.TYPE;
        this.connectionId = connectionId;
        this.messageId = messageId;
        this.payload = payload;
    }


    public String getConnectionId()
    {
        return connectionId;
    }



    public String getMessageId()
    {
        return messageId;
    }



    public Object getPayload()
    {
        return payload;
    }


    public String getType()
    {
        return type;
    }
}

