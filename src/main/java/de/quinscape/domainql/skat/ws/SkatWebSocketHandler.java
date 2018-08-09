package de.quinscape.domainql.skat.ws;


import de.quinscape.domainql.skat.model.message.IncomingMessage;
import de.quinscape.domainql.skat.runtime.service.AppAuthentication;
import de.quinscape.domainql.skat.runtime.message.ConnectionCloseListener;
import de.quinscape.domainql.skat.runtime.message.IncomingMessageHandler;
import de.quinscape.spring.jsview.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.svenson.JSONParseException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Handles websockets. It is an extension of Spring's TextWebSocketHandler to handle the skat game websocket traffic
 * without Socket.io/STOMP.
 */
public class SkatWebSocketHandler
    extends TextWebSocketHandler
{
    private final static Logger log = LoggerFactory.getLogger(SkatWebSocketHandler.class);

    private static final String CONNECTION_ID = SkatWebSocketHandler.class.getName() + ":cid";


    private final CopyOnWriteArrayList<ConnectionCloseListener> listeners = new CopyOnWriteArrayList<>();
    private final ConcurrentMap<String, SkatClientConnection> connections = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, IncomingMessageHandler> handlers = new ConcurrentHashMap<>();


    public SkatWebSocketHandler(List<IncomingMessageHandler> handlers)
    {
        for (IncomingMessageHandler handler : handlers)
        {
            this.handlers.put(handler.getMessageType(), handler);
        }
        
        log.info("Starting SkatWebSocketHandler, handlers = {}", this.handlers);
    }


    public SkatClientConnection getClientConnection(String connectionId)
    {
        return connections.get(connectionId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
    {
        log.debug("handleTextMessage: {}, session = {}  ", message, session);

        final String cid = getCid(session);
        if (cid == null)
        {
            throw new IllegalArgumentException("No cid registered in WebSocketSession");
        }

        final SkatClientConnection connection = connections.get(cid);
        if (connection == null)
        {
            throw new IllegalArgumentException("No connection registered for cid '" + cid + "'");
        }

        try
        {
            final IncomingMessage msg = JSONUtil.DEFAULT_PARSER.parse(IncomingMessage.class, message.getPayload());

            final String messageType = msg.getType();
            final IncomingMessageHandler handler = handlers.get(messageType);
            if (handler == null)
            {
                throw new IllegalStateException("No handler for " + msg);
            }

            setupSecurityContext(connection);

            handler.handle(msg, connection);
        }
        catch (JSONParseException e)
        {
            log.error("Error parsing '" + message.getPayload() + "'.", e);
        }
    }


    private String getCid(WebSocketSession session)
    {
        return (String)session.getAttributes().get(CONNECTION_ID);
    }


    /**
     * Sets zp the spring security context for the websocket handling with the original auth we remembered when registering
     * the connection.
     *
     * @param connection
     */
    private void setupSecurityContext(SkatClientConnection connection)
    {
        if (connection == null)
        {
            throw new IllegalArgumentException("connection can't be null");
        }


        final AppAuthentication auth = connection.getAuth();

        //log.debug("Reregistering {}", auth);

        SecurityContextHolder.setContext(
            new SecurityContextImpl(

                new PreAuthenticatedAuthenticationToken(
                    auth,
                    null,
                    auth.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
                )
            )
        );
    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session)
    {
        final String query = session.getUri().getQuery();

        if (!query.startsWith("cid=") && query.lastIndexOf('=') != 3)
        {
            throw new IllegalStateException("Invalid query with cid as single parameter: " + session.getUri().toString());
        }

        final String cid = query.substring(4);

        log.debug("afterConnectionEstablished: session = {}, cid = '{}'", session, cid);

        final SkatClientConnection connection = connections.get(cid);
        if (connection == null)
        {
            throw new IllegalStateException("Connection '" + cid + "' not preregistered with auth.");
        }
        connection.initialize(session);

        session.getAttributes().put(CONNECTION_ID, cid);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) 
    {
        log.debug("afterConnectionClosed: session = {}, status = {}", session, status);

        final String cid = getCid(session);
        if (cid != null)
        {
            final SkatClientConnection conn = connections.remove(cid);

            if (conn != null)
            {
                for (ConnectionCloseListener listener : listeners)
                {
                    listener.onClose(this, conn);
                }
            }
        }
    }


    /**
     * Registers the given ConnectionCloseListener to be notified of every closed connection.
     *
     * @param listener      listener
     */
    public void register(ConnectionCloseListener listener)
    {
        listeners.add(listener);
    }


    /**
     * Preregisters the given connection id to be associated with the given authentication.
     *
     * When the client opens the websocket connection it will pass back the id so we can
     * associate the given out with websocket messages.
     *
     */
    public void register(SkatClientConnection skatClientConnection)
    {
        connections.put(skatClientConnection.getConnectionId(), skatClientConnection);
    }


    @Override
    public boolean supportsPartialMessages()
    {
        return false;
    }
}
