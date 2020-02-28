package io.quarkus.smallrye.graphql.runtime;

import io.smallrye.graphql.execution.ExecutionService;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.enterprise.inject.spi.CDI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import org.jboss.logging.Logger;

/**
 * Handler that return the execution
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class SmallRyeGraphQLExecutionHandler implements Handler<RoutingContext> {
    private static final Logger LOG = Logger.getLogger(SmallRyeGraphQLExecutionHandler.class);
    
    private final SmallRyeGraphQLConfig smallRyeGraphQLConfig;

    public SmallRyeGraphQLExecutionHandler(SmallRyeGraphQLConfig smallRyeGraphQLConfig) {
        this.smallRyeGraphQLConfig = smallRyeGraphQLConfig;
    }
    
    @Override
    public void handle(final RoutingContext ctx) {
        LOG.error(">>>>>>>>> Handling execution !");
        HttpServerRequest request = ctx.request();
        HttpServerResponse response = ctx.response();
        switch (request.method()) {
            case OPTIONS:
                response.headers().set(HttpHeaders.ALLOW, getAllowedMethods());
                break;
            case POST:
                String postResponse = doRequest(ctx.getBodyAsString());
                LOG.warn("********** postResponse = " + postResponse);
                response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
                response.setStatusCode(200).end(Buffer.buffer(postResponse));
                break;
            case GET:
                if(smallRyeGraphQLConfig.allowGet){
                    List<String> queries = ctx.queryParam(QUERY);
                    if(queries!=null && !queries.isEmpty()){
                        String getResponse = doRequest(queries.get(0));
                        LOG.warn("********** getResponse = " + getResponse);
                        response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
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
        if(smallRyeGraphQLConfig.allowGet){
            return "GET, POST, OPTIONS";
        }else{
            return "POST, OPTIONS";
        }
    }
    
    private String doRequest(String body){
        LOG.warn("********** real body = " + body);
        if(body==null || body.isEmpty()){
            body = dummyBody();
        }   LOG.warn("********** dummy body = " + body);
        return doRequest(body.getBytes());
    }
    
    private String doRequest(final byte[] body){
        //boolean activated = RequestScopeHelper.activeRequestScope();
        try (ByteArrayInputStream input = new ByteArrayInputStream(body)) {
            final JsonReader jsonReader = Json.createReader(input);
            JsonObject jsonInput = jsonReader.readObject();
            ExecutionService executionService = CDI.current().select(ExecutionService.class).get();
            JsonObject outputJson = executionService.execute(jsonInput);
            if (outputJson != null) {
                try (StringWriter output = new StringWriter()) {
                    final JsonWriter jsonWriter = Json.createWriter(output);
                    jsonWriter.writeObject(outputJson);
                    output.flush();
                    return output.toString();
                }
            }
            throw new RuntimeException("Response is null");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            //if (activated) {
            //    Arc.container().requestContext().terminate();
            //}
        }
        
    }
    
    
    
    
    
    
    
    
    
    // Hardcoded 
    private String dummyBody(){
        return getPayload("{\n" +
        "  ping {\n" +
        "    message\n" +
        "  }\n" +
        "}");
    }
    
    private String getPayload(String query){
        JsonObject jsonObject = createRequestBody(query);
        return jsonObject.toString();   
    }
    
    private JsonObject createRequestBody(String graphQL){
        return createRequestBody(graphQL, null);
    }
    
    private JsonObject createRequestBody(String graphQL, JsonObject variables){
        // Create the request
        if(variables==null || variables.isEmpty()) {
            variables = Json.createObjectBuilder().build();
        }
        return Json.createObjectBuilder().add(QUERY, graphQL).add(VARIABLES, variables).build();
    }
    
    private static final String QUERY = "query";
    private static final String VARIABLES = "variables";
    
}