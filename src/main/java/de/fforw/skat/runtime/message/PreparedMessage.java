package de.fforw.skat.runtime.message;

import de.quinscape.spring.jsview.util.JSONUtil;
import org.svenson.JSONable;

/**
 * Special outgoing message that does an early JSONification of another message to be able to generate
 * the JSON dump of an object within a synchronized block synchronized of this object, but not doing to actual message
 * communication within that lock.
 */
public final class PreparedMessage
    extends OutgoingMessage
    implements JSONable
{

    private final String connectionId;

    private final String json;

    public PreparedMessage(String connectionId, OutgoingMessage outgoingMessage)
    {
        super(outgoingMessage.getType(), outgoingMessage.getPayload(), outgoingMessage.getError(), outgoingMessage.getId());
        this.connectionId = connectionId;
        this.json = JSONUtil.DEFAULT_GENERATOR.forValue(outgoingMessage);
    }


    public String getConnectionId()
    {
        return connectionId;
    }


    @Override
    public String toJSON()
    {
        return json;
    }
}
