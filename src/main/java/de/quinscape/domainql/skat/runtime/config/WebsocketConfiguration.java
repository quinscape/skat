package de.quinscape.domainql.skat.runtime.config;

import de.quinscape.domainql.skat.runtime.message.GraphQLMessageHandler;
import de.quinscape.domainql.skat.ws.SkatWebSocketHandler;
import graphql.GraphQL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;

@Configuration
@EnableWebSocket
public class WebsocketConfiguration
    implements WebSocketConfigurer
{
    private final GraphQL graphQL;


    public WebsocketConfiguration(
        @Lazy graphql.GraphQL graphQL
    )
    {
        this.graphQL = graphQL;
    }


    /**
     * We don't want to deal with all the socket.js/stomp stuff for now, so we register our own spring
     * {@link TextWebSocketHandler} implementation.
     *
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        final SkatWebSocketHandler webSocketHandler = skatSocketHandler();

        registry.addHandler(webSocketHandler, "/skat-ws");
    }

    @Bean
    public SkatWebSocketHandler skatSocketHandler()
    {
        return new SkatWebSocketHandler(
            Arrays.asList(
                new GraphQLMessageHandler(
                    graphQL
                )
            )
        );
    }
}
