package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.model.message.ChannelUpdateAction;
import de.quinscape.domainql.skat.model.message.OutgoingMessage;
import de.quinscape.domainql.skat.runtime.message.PreparedMessage;
import de.quinscape.domainql.skat.ws.SkatClientConnection;
import org.springframework.web.socket.WebSocketSession;

import java.util.function.Consumer;

public class TestSkatClientConnection
    implements SkatClientConnection
{
    private final String connectionId;

    private final AppAuthentication auth;

    private final Consumer<ClientState> consumer;


    public TestSkatClientConnection(
        String connectionId, AppAuthentication auth,
        Consumer<ClientState> consumer
    )
    {
        this.connectionId = connectionId;
        this.auth = auth;
        this.consumer = consumer;
    }


    @Override
    public void initialize(WebSocketSession session)
    {
        
    }


    @Override
    public WebSocketSession getSession()
    {
        return null;
    }


    @Override
    public String getConnectionId()
    {
        return connectionId;
    }


    @Override
    public AppAuthentication getAuth()
    {
        return auth;
    }


    @Override
    public void send(OutgoingMessage message)
    {
        message = PreparedMessage.unwrap(message);

        final Object payload = message.getPayload();
        if (payload instanceof ChannelUpdateAction)
        {
            consumer.accept(new ClientState(((ChannelUpdateAction) payload).getChannel(), ((ChannelUpdateAction) payload).getHand()));
        }
        else
        {
            throw new IllegalStateException("Unhandled message:" + message);
        }


    }


    @Override
    public void respond(String messageId, Object payload, String error)
    {

    }


    @Override
    public void respond(String messageId, Object result)
    {

    }
}
