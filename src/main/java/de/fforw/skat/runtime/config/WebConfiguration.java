package de.fforw.skat.runtime.config;

import de.fforw.skat.util.Base32;
import de.fforw.skat.ws.SkatWebSocketHandler;
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

@Configuration
public class WebConfiguration
    implements WebMvcConfigurer
{
    private final ServletContext servletContext;

    private final ResourceLoader resourceLoader;

    private final GraphQLSchema graphQLSchema;

    private final SkatWebSocketHandler skatWebSocketHandler;


    @Autowired
    public WebConfiguration(
        ServletContext servletContext,
        GraphQLSchema graphQLSchema,
        ResourceLoader resourceLoader,
        @Lazy SkatWebSocketHandler skatWebSocketHandler
    )
    {
        this.servletContext = servletContext;
        this.graphQLSchema = graphQLSchema;
        this.resourceLoader = resourceLoader;

        this.skatWebSocketHandler = skatWebSocketHandler;
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



                    final String connectionId = Base32.uuid();
                    ctx.provideViewData("connectionId", connectionId);

                    skatWebSocketHandler.register(connectionId, auth);
                })

                .build()
        );
    }


}
