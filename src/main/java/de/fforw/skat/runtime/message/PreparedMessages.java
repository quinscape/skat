package de.fforw.skat.runtime.message;

import de.fforw.skat.ws.SkatClientConnection;
import de.fforw.skat.ws.SkatWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

public class PreparedMessages
{
    private final static List<PreparedMessage> preparedMessages = new ArrayList<>();

    public PreparedMessages()
    {

    }

    public void add(String connectionId, OutgoingMessage outgoingMessage)
    {
        preparedMessages.add(new PreparedMessage(connectionId, outgoingMessage));
    }
    public void addAll(List<PreparedMessage> preparedMessages)
    {
        preparedMessages.addAll(preparedMessages);
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
