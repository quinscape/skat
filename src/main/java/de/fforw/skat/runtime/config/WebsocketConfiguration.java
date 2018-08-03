package de.fforw.skat.runtime.config;

import de.fforw.skat.runtime.message.GraphQLMessageHandler;
import de.fforw.skat.ws.SkatWebSocketHandler;
import graphql.GraphQL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Collections;

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


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        final SkatWebSocketHandler webSocketHandler = skatSocketHandler();

        registry.addHandler(webSocketHandler, "/skat-ws");
    }

    @Bean
    public SkatWebSocketHandler skatSocketHandler()
    {
        final SkatWebSocketHandler skatWebSocketHandler = new SkatWebSocketHandler(
            Collections.singleton(
                new GraphQLMessageHandler(
                    graphQL
                )

            )
        );

        return skatWebSocketHandler;
    }
}
