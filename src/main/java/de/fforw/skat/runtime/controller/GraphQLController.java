package de.fforw.skat.runtime.controller;

import de.quinscape.spring.jsview.util.JSONUtil;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class GraphQLController
{
    private final static Logger log = LoggerFactory.getLogger(GraphQLController.class);


    private final GraphQL graphQL;

    /**
     * URI for the normal application data usage. Is always under general Spring security protection which includes
     * CSRF protection.
     */
    public final static String GRAPHQL_URI = "/graphql";

    /**
     * Special development GraphQL end point that is active if 
     */
    public final static String GRAPHQL_DEV_URI = "/graphql-dev";


    @Autowired
    public GraphQLController(
        @Lazy GraphQL graphQL
    )
    {
        this.graphQL = graphQL;
    }


    @RequestMapping(value = GRAPHQL_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> serveGraphQL(@RequestBody Map body)
    {
        return executeGraphQLQuery(body);
    }


    /**
     * Special development graphql endpoint that is accessible without CSRF protection if the environment property
     * <em>skat.graphql.dev</em> is set to <code>true</code>.
     *
     * This dedicated end point allows access to GraphQL queries from the IDE (without session/CSRF). 
     *
     * @param body      request body
     * @return GraphQL response entity
     */
    @Profile("dev")
    @RequestMapping(value = GRAPHQL_DEV_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> serveGraphQLDev(@RequestBody Map body)
    {
        return executeGraphQLQuery(body);
    }


    private ResponseEntity<String> executeGraphQLQuery(@RequestBody Map body)
    {
        String query = (String) body.get("query");
        Map<String, Object> variables = (Map<String, Object>) body.get("variables");
        //log.debug("/graphql: query = {}, vars = {}", query, variables);

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .variables(variables)
            .build();

        ExecutionResult executionResult = graphQL.execute(executionInput);

        // result may contain data and/or errors
        Object result = executionResult.toSpecification();
        return new ResponseEntity<String>(
            JSONUtil.DEFAULT_GENERATOR.forValue(
                result
            ),
            executionResult.getErrors().size() == 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST
        );
    }
}
