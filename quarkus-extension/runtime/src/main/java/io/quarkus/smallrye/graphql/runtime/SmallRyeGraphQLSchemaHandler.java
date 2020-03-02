package io.quarkus.smallrye.graphql.runtime;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Handler that return the generated schema
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class SmallRyeGraphQLSchemaHandler implements Handler<RoutingContext> {
    private static final String ALLOWED_METHODS = "GET, OPTIONS";
    private final String schema;
            
    public SmallRyeGraphQLSchemaHandler(String graphQLSchema){ 
        this.schema = graphQLSchema;
    }
    
    @Override
    public void handle(RoutingContext event) {
        HttpServerRequest request = event.request();
        HttpServerResponse response = event.response();
        if (request.method().equals(HttpMethod.OPTIONS)) {
            response.headers().set(HttpHeaders.ALLOW, ALLOWED_METHODS);
        } else if (request.method().equals(HttpMethod.GET)) {
            response.headers().set(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.end(Buffer.buffer(schema));
        }
    }
}