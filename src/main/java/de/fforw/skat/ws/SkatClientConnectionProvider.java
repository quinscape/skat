package de.fforw.skat.ws;

import de.quinscape.domainql.param.ParameterProvider;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.util.StringUtils;

public final class SkatClientConnectionProvider
    implements ParameterProvider
{

    private final SkatWebSocketHandler skatWebSocketHandler;


    public SkatClientConnectionProvider(SkatWebSocketHandler skatWebSocketHandler)
    {

        this.skatWebSocketHandler = skatWebSocketHandler;
    }


    @Override
    public Object provide(DataFetchingEnvironment environment)
    {
        final String connectionId = environment.getContext();
        if (!StringUtils.hasText(connectionId))
        {
            throw new IllegalStateException("No connection id found in data fetching environment context.'");
        }

        final SkatClientConnection clientConnection = skatWebSocketHandler.getClientConnection(connectionId);
        if (clientConnection == null)
        {
            throw new IllegalStateException("No client connection for id '" + connectionId + "' from data fetching environment context.");
        }

        return clientConnection;
    }
}
