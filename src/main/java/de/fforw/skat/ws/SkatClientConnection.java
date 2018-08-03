package de.fforw.skat.ws;

import de.fforw.skat.model.SkatRuntimeException;
import de.fforw.skat.runtime.config.AppAuthentication;
import de.fforw.skat.runtime.message.OutgoingMessage;
import de.fforw.skat.runtime.message.Response;
import de.quinscape.spring.jsview.util.JSONUtil;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.svenson.JSONProperty;

import java.io.IOException;

public class SkatClientConnection
{
    private volatile WebSocketSession session;

    private final String connectionId;

    private final AppAuthentication auth;


    public SkatClientConnection(String connectionId, AppAuthentication auth)
    {
        this.connectionId = connectionId;
        this.auth = auth;
    }

    public void initialize(WebSocketSession session)
    {
        this.session = session;
    }


    @JSONProperty(ignore = true)
    public WebSocketSession getSession()
    {
        return session;
    }


    public String getConnectionId()
    {
        return connectionId;
    }


    public AppAuthentication getAuth()
    {
        return auth;
    }


    public void send(OutgoingMessage message)
    {
        try
        {
            session.sendMessage(
                new TextMessage(
                    JSONUtil.DEFAULT_GENERATOR.forValue(
                        message
                    )
                )
            );
        }
        catch (IOException e)
        {
            throw new SkatRuntimeException("Error sending websocket message", e);
        }
    }


    public void respond(String messageId, Object payload, String error)
    {
        final Response response = new Response(messageId, payload, error);

        send(
            response
        );
    }


    public void respond(String messageId, Object result)
    {
        respond(messageId, result, null);
    }
}
