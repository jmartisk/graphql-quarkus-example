package io.quarkus.smallrye.graphql.runtime;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import io.smallrye.graphql.execution.ExecutionService;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import javax.enterprise.inject.spi.CDI;
import org.jboss.logging.Logger;

/**
 * Handler that return the generated schema
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class SmallRyeGraphQLSchemaHandler implements Handler<RoutingContext> {
    private static final Logger LOG = Logger.getLogger(SmallRyeGraphQLExecutionHandler.class);
    
    private static final String ALLOWED_METHODS = "GET, OPTIONS";

    //private final GraphQLSchema graphQLSchema;
            
    //public SmallRyeGraphQLSchemaHandler(GraphQLSchema graphQLSchema){
    //    this.graphQLSchema = graphQLSchema;
    //}
    
    @Override
    public void handle(RoutingContext event) {
        LOG.error(">>>>>>>>> Handling schema !");
        HttpServerRequest request = event.request();
        HttpServerResponse response = event.response();
        if (request.method().equals(HttpMethod.OPTIONS)) {
            response.headers().set(HttpHeaders.ALLOW, ALLOWED_METHODS);
        } else if (request.method().equals(HttpMethod.GET)) {
            response.headers().set(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
            ExecutionService executionService = CDI.current().select(ExecutionService.class).get();
            response.end(Buffer.buffer(schemaPrinter.print(executionService.getGraphQLSchema())));
        }
    }
    
    private final SchemaPrinter schemaPrinter = new SchemaPrinter();
}