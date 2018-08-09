package de.quinscape.domainql.skat.runtime.message;

import de.quinscape.domainql.skat.model.message.OutgoingMessage;
import de.quinscape.domainql.skat.ws.SkatClientConnection;
import de.quinscape.domainql.skat.ws.SkatWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

public class PreparedMessages
{
    private final List<PreparedMessage> preparedMessages = new ArrayList<>();

    public PreparedMessages()
    {

    }

    public void add(String connectionId, OutgoingMessage outgoingMessage)
    {
        preparedMessages.add(new PreparedMessage(connectionId, outgoingMessage));
    }


    public List<PreparedMessage> getMessages()
    {
        return preparedMessages;
    }


    public void addAll(List<PreparedMessage> preparedMessages)
    {
        this.preparedMessages.addAll(preparedMessages);
    }

    public void sendAll(SkatWebSocketHandler skatWebSocketHandler)
    {
        for (PreparedMessage preparedMessage : preparedMessages)
        {
            final SkatClientConnection connection = skatWebSocketHandler.getClientConnection(preparedMessage.getConnectionId());
            if (connection != null)
            {
                connection.send(preparedMessage);
            }
        }
    }
}
