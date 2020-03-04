package io.quarkus.smallrye.graphql.deployment;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.common.http.TestHTTPResource;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.BodyHandler;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
import javax.json.Json;
import javax.json.JsonObject;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.net.URL;
import org.junit.jupiter.api.Assertions;

/**
 * Basic tests. POST and GET
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@ExtendWith({
    VertxExtension.class
})
public class GraphQLTest {
    private static final Logger LOG = Logger.getLogger(GraphQLTest.class);
    
    @TestHTTPResource("/graphql") 
    URL url;
    
    @RegisterExtension
    static QuarkusUnitTest test = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestResource.class,TestPojo.class)
                    .addAsResource(new StringAsset(getPropertyAsString()), "application.properties")
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml"));
    
    @Test
    public void testSchema(VertxTestContext testContext){
        Vertx vertx = Vertx.vertx();
        WebClient client = WebClient.create(vertx);

        client.get(url.getPort(), url.getHost(), url.getPath() + "/schema.graphql")
            .putHeader(ACCEPT, MEDIATYPE_TEXT)
            .putHeader(CONTENT_TYPE, MEDIATYPE_TEXT)                
            .as(BodyCodec.string())
            .send(testContext.succeeding(response -> testContext.verify(() -> {
                Assertions.assertEquals(200, response.statusCode());
                
                LOG.info("Schema Response: \n" + response.body());
                
                Assertions.assertTrue(response.body().contains("\"Query root\""));
                Assertions.assertTrue(response.body().contains("type Query {"));
                Assertions.assertTrue(response.body().contains("ping: TestPojo"));
                testContext.completeNow();
            })));

    }
    
    @Test 
    public void testPost(VertxTestContext testContext) {
        
        String pingRequest = getPayload("{\n" +
        "  ping {\n" +
        "    message\n" +
        "  }\n" +
        "}");
        
        
        Vertx vertx = Vertx.vertx();
        WebClient client = WebClient.create(vertx);

        client.post(url.getPort(), url.getHost(), url.getPath())
            .putHeader(ACCEPT, MEDIATYPE_JSON)
            .putHeader(CONTENT_TYPE, MEDIATYPE_JSON)
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer(pingRequest), testContext.succeeding(response -> testContext.verify(() -> {
                Assertions.assertEquals(200, response.statusCode());
                
                LOG.info("POST Response: \n" + response.body());
                
                Assertions.assertTrue(response.body().contains("{\"data\":{\"ping\":{\"message\":\"pong\"}}}"));
                testContext.completeNow();
                
            })));
        
    }
    
    @Test 
    public void testGet(VertxTestContext testContext) {
        String fooRequest = getPayload("{\n" +
        "  foo {\n" +
        "    message\n" +
        "  }\n" +
        "}");
              
        Vertx vertx = Vertx.vertx();
        WebClient client = WebClient.create(vertx);

        client.get(url.getPort(), url.getHost(), url.getPath())
            .putHeader(ACCEPT, MEDIATYPE_JSON)
            .putHeader(CONTENT_TYPE, MEDIATYPE_JSON)
            .addQueryParam(QUERY, fooRequest)
            .as(BodyCodec.string())
             
            .send(testContext.succeeding(response -> testContext.verify(() -> {
                Assertions.assertEquals(200, response.statusCode());
                
                LOG.info("GET Response: \n" + response.body());
                
                Assertions.assertTrue(response.body().contains("{\"data\":{\"foo\":{\"message\":\"bar\"}}}"));
                testContext.completeNow();
            })));
        
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
    
    private static String getPropertyAsString(){    
        try {
            StringWriter writer = new StringWriter();
            PROPERTIES.store(writer,"Test Properties");
            return writer.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MEDIATYPE_JSON = "application/json";
    private static final String MEDIATYPE_TEXT = "text/plain";
    private static final String QUERY = "query";
    private static final String VARIABLES = "variables";
    
    private static final Properties PROPERTIES = new Properties();
    static {
        PROPERTIES.put("quarkus.smallrye-graphql.allow-get", "true");
        PROPERTIES.put("quarkus.smallrye-graphql.print-data-fetcher-exception", "true");  
    }
}