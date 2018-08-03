package de.fforw.skat.runtime.message;

import de.fforw.skat.ws.SkatClientConnection;
import de.fforw.skat.ws.SkatWebSocketHandler;

public interface ConnectionCloseListener
{

    void onClose(SkatWebSocketHandler skatWebSocketHandler, SkatClientConnection skatClientConnection);
}
