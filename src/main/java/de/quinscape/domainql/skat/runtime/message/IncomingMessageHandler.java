package de.quinscape.domainql.skat.runtime.message;

import de.quinscape.domainql.skat.model.message.IncomingMessage;
import de.quinscape.domainql.skat.ws.SkatClientConnection;

/**
 * Implemented by classes wanting to handle one type of messages.
 */
public interface IncomingMessageHandler
{
    /**
     * Unique message type value associated with the messages being handled
     * @return
     */
    String getMessageType();

    /**
     * Handles the incoming message
     *
     * @param msg           incoming message
     * @param connection    client connection
     */
    void handle(IncomingMessage msg, SkatClientConnection connection);
}
