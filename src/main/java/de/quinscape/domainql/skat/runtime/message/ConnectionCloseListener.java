package de.quinscape.domainql.skat.runtime.message;

import de.quinscape.domainql.skat.ws.SkatClientConnection;
import de.quinscape.domainql.skat.ws.SkatWebSocketHandler;

public interface ConnectionCloseListener
{

    void onClose(SkatWebSocketHandler skatWebSocketHandler, SkatClientConnection skatClientConnection);
}
