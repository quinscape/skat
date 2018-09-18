package de.quinscape.domainql.skat.runtime.config;

import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;
import de.quinscape.domainql.skat.runtime.service.AppAuthentication;
import de.quinscape.domainql.skat.runtime.service.ShufflingService;
import de.quinscape.domainql.skat.runtime.service.ShufflingStrategyInfo;
import de.quinscape.domainql.skat.util.Base32;
import de.quinscape.domainql.skat.util.JSONWrapper;
import de.quinscape.domainql.skat.ws.DefaultSkatClientConnection;
import de.quinscape.domainql.skat.ws.SkatWebSocketHandler;
import de.quinscape.domainql.preload.PreloadedGraphQLQueryProvider;
import de.quinscape.domainql.schema.SchemaDataProvider;
import de.quinscape.spring.jsview.JsViewResolver;
import de.quinscape.spring.jsview.loader.ResourceLoader;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import java.util.stream.Collectors;

@Configuration
public class WebConfiguration
    implements WebMvcConfigurer
{

    private final ServletContext servletContext;

    private final ResourceLoader resourceLoader;

    private final GraphQLSchema graphQLSchema;

    private final SkatWebSocketHandler skatWebSocketHandler;

    private final JSONWrapper shufflingStrategyInfos;


    @Autowired
    public WebConfiguration(
        ServletContext servletContext,
        GraphQLSchema graphQLSchema,
        ResourceLoader resourceLoader,
        @Lazy SkatWebSocketHandler skatWebSocketHandler,
        ShufflingService shufflingService
    )
    {
        this.servletContext = servletContext;
        this.graphQLSchema = graphQLSchema;
        this.resourceLoader = resourceLoader;

        this.skatWebSocketHandler = skatWebSocketHandler;

        final ShufflingStrategy defaultStrategy = shufflingService.getStrategy(null);

        shufflingStrategyInfos = JSONWrapper.wrap(
            shufflingService.getStrategyNames().stream()
            .map(
                name -> {
                    final ShufflingStrategy strategy = shufflingService.getStrategy(name);
                    return new ShufflingStrategyInfo(name, strategy, strategy == defaultStrategy);
                }
            )
            .collect(
                Collectors.toList()
            )
        );
    }


    @Override
    public void configureViewResolvers(ViewResolverRegistry registry)
    {
        registry.viewResolver(
            JsViewResolver.newResolver(servletContext, "WEB-INF/template.html")
                .withResourceLoader(resourceLoader)

                .withViewDataProvider(
                    new PreloadedGraphQLQueryProvider(
                        graphQLSchema,
                        resourceLoader
                    )
                )
                .withViewDataProvider(
                    new SchemaDataProvider(
                        graphQLSchema
                    )
                )
                .withViewDataProvider(ctx -> {

                    final CsrfToken token = (CsrfToken) ctx.getRequest().getAttribute("_csrf");
                    final AppAuthentication auth = AppAuthentication.current();

                    ctx.provideViewData("contextPath", ctx.getRequest().getContextPath());
                    ctx.provideViewData("authentication", auth);
                    ctx.provideViewData("csrfToken", new ClientCrsfToken(token));

                    ctx.provideViewData("shufflingStrategies", shufflingStrategyInfos);

                    final String connectionId = Base32.uuid();
                    ctx.provideViewData("connectionId", connectionId);

                    skatWebSocketHandler.register(
                        new DefaultSkatClientConnection(
                            connectionId,
                            auth
                        )
                    );
                })

                .build()
        );
    }


}
