package de.fforw.skat.runtime.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Container for incoming messages.
 */
public class OutgoingMessage
{
    private final static Logger log = LoggerFactory.getLogger(OutgoingMessage.class);


    private final Object payload;

    private final Object error;

    private final String type;

    private final int id;
    private final static AtomicInteger counter = new AtomicInteger();
    protected static int nextId()
    {
        return counter.incrementAndGet();
    }

    public OutgoingMessage(String type, Object payload, Object error)
    {
        this(type, payload, error, nextId());
    }

    protected OutgoingMessage(String type, Object payload, Object error, int id)
    {
        this.type = type;
        this.payload = payload;
        this.error = error;
        this.id = id;

        log.debug("Creating OutgoingMessage: type = {}, payload = {}, error = {}, id = {}", type, payload, error, id);
    }

    public Object getPayload()
    {
        return payload;
    }


    public Object getError()
    {
        return error;
    }


    public String getType()
    {
        return type;
    }

    public static OutgoingMessage error(Object error)
    {
        return new OutgoingMessage(OutgoingMessageType.ERROR, null, error);
    }

    public int getId()
    {
        return id;
    }
}

