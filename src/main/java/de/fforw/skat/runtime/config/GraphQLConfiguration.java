package de.fforw.skat.runtime.config;

import de.quinscape.domainql.DomainQL;
import de.quinscape.domainql.annotation.GraphQLLogic;
import de.quinscape.domainql.config.SourceField;
import de.quinscape.domainql.config.TargetField;
import de.fforw.skat.domain.Public;
import de.quinscape.spring.jsview.loader.ResourceLoader;
import de.quinscape.spring.jsview.loader.ServletResourceLoader;
import graphql.schema.GraphQLSchema;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Collection;


import static de.fforw.skat.domain.Tables.*;
/**
 * Exemplary configuration of GraphQL in a project.
 */
@Configuration
public class GraphQLConfiguration
{
    private final static Logger log = LoggerFactory.getLogger(GraphQLConfiguration.class);


    private final ApplicationContext applicationContext;
    private final ServletContext servletContext;
    private final DSLContext dslContext;


    @Autowired
    public GraphQLConfiguration(
        ApplicationContext applicationContext,
        ServletContext servletContext,
        DSLContext dslContext
    )
    {
        this.applicationContext = applicationContext;
        this.servletContext = servletContext;
        this.dslContext = dslContext;
    }


    @Bean
    public GraphQLSchema graphQLSchema(
    )

    {
        final Collection<Object> logicBeans = applicationContext.getBeansWithAnnotation(GraphQLLogic.class).values();

        return DomainQL.newDomainQL(dslContext)
            .logicBeans(logicBeans)
            .objectTypes(Public.PUBLIC)
            .createMirrorInputTypes(true)
            // configure object creation for schema relationships
//            .configureRelation( FOO.TYPE_ID, SourceField.OBJECT_AND_SCALAR, TargetField.NONE)
//            .configureRelation( FOO.OWNER_ID, SourceField.OBJECT_AND_SCALAR, TargetField.MANY)
            .buildGraphQLSchema();
    }

    @Bean
    public ResourceLoader resourceLoader() throws IOException
    {
        return new ServletResourceLoader(
            servletContext,
            "/",
            true
        );
    }
    
    @EventListener(ContextStoppedEvent.class)
    public void contextRefreshed(ContextStoppedEvent event) throws IOException
    {
        final ResourceLoader resourceLoader = resourceLoader();
        resourceLoader.shutDown();
    }
}

