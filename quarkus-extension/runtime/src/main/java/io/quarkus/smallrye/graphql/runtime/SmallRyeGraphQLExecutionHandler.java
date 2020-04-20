package io.quarkus.smallrye.graphql.runtime;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.enterprise.inject.spi.CDI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import org.jboss.logging.Logger;
import io.smallrye.graphql.execution.ExecutionService;

/**
 * Handler that does the execution of GraphQL Requests
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class SmallRyeGraphQLExecutionHandler implements Handler<RoutingContext> {
    private static final Logger LOG = Logger.getLogger(SmallRyeGraphQLExecutionHandler.class);
    
    private static boolean allowGet = false;
    
    public SmallRyeGraphQLExecutionHandler(boolean allowGet) {
        this.allowGet = allowGet;
    }
    
    @Override
    public void handle(final RoutingContext ctx) {
        LOG.error("===== Executing operation =====");
        HttpServerRequest request = ctx.request();
        HttpServerResponse response = ctx.response();
        
        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        
        switch (request.method()) {
            case OPTIONS:
                response.headers().set(HttpHeaders.ALLOW, getAllowedMethods());
                break;
            case POST:
                String postResponse = doRequest(ctx.getBodyAsString());
                response.setStatusCode(200).end(Buffer.buffer(postResponse));
                break;
            case GET:
                if(allowGet){
                    List<String> queries = ctx.queryParam(QUERY);
                    if(queries!=null && !queries.isEmpty()){
                        String getResponse = doRequest(queries.get(0));
                        response.setStatusCode(200).end(Buffer.buffer(getResponse));
                    }else{
                        response.setStatusCode(204).setStatusMessage("Provide a query parameter").end();
                    }
                }else{
                    response.setStatusCode(405).setStatusMessage("GET Queries is not enabled").end();
                }   break;
            default:
                break;
        }
    }
    
    private String getAllowedMethods(){
        if(allowGet){
            return "GET, POST, OPTIONS";
        }else{
            return "POST, OPTIONS";
        }
    }
    
    private String doRequest(final String body){
        try (StringReader input = new StringReader(body);
            final JsonReader jsonReader = Json.createReader(input)){
            JsonObject jsonInput = jsonReader.readObject();
            ExecutionService executionService = CDI.current().select(ExecutionService.class).get();
            JsonObject outputJson = executionService.execute(jsonInput);
            if (outputJson != null) {
                try (StringWriter output = new StringWriter();
                    final JsonWriter jsonWriter = Json.createWriter(output)){
                    jsonWriter.writeObject(outputJson);
                    output.flush();
                    return output.toString();
                }
            }
            throw new RuntimeException("Response is null");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }    
    }
    
    private static final String QUERY = "query";
    
}