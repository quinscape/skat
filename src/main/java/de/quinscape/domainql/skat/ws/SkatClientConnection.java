package de.quinscape.domainql.skat.ws;

import de.quinscape.domainql.skat.model.message.OutgoingMessage;
import de.quinscape.domainql.skat.runtime.service.AppAuthentication;
import org.springframework.web.socket.WebSocketSession;
import org.svenson.JSONProperty;

public interface SkatClientConnection
{
    void initialize(WebSocketSession session);

    @JSONProperty(ignore = true)
    WebSocketSession getSession();

    String getConnectionId();

    AppAuthentication getAuth();

    void send(OutgoingMessage message);

    void respond(String messageId, Object payload, String error);

    void respond(String messageId, Object result);
}
