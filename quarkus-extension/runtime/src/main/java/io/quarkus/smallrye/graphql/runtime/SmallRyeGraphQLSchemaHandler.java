package io.quarkus.smallrye.graphql.runtime;

import graphql.schema.GraphQLSchema;
import io.smallrye.graphql.bootstrap.Config;
import io.smallrye.graphql.execution.SchemaPrinter;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import javax.enterprise.inject.spi.CDI;

/**
 * Handler that return the generated schema
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class SmallRyeGraphQLSchemaHandler implements Handler<RoutingContext> {
    private static final String ALLOWED_METHODS = "GET, OPTIONS";
    private static final String CONTENT_TYPE = "text/plain; charset=UTF-8";
    
    private final SchemaPrinter schemaPrinter;
    
    public SmallRyeGraphQLSchemaHandler(SmallRyeGraphQLConfig config){
        schemaPrinter = new SchemaPrinter(new Config(){});
    }
    
    @Override
    public void handle(RoutingContext event) {
        GraphQLSchema graphQLSchema = CDI.current().select(GraphQLSchema.class).get();
        String schemaString = schemaPrinter.print(graphQLSchema);
        
        HttpServerRequest request = event.request();
        HttpServerResponse response = event.response();
        if (request.method().equals(HttpMethod.OPTIONS)) {
            response.headers().set(HttpHeaders.ALLOW, ALLOWED_METHODS);
        } else if (request.method().equals(HttpMethod.GET)) {
            response.headers().set(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
            response.end(Buffer.buffer(schemaString));
        }
    }
}