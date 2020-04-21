package io.quarkus.smallrye.graphql.deployment;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.CoreMatchers;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.jupiter.api.Assertions;

/**
 * Basic tests. POST and GET
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class GraphQLTest {
    private static final Logger LOG = Logger.getLogger(GraphQLTest.class);
    
    @RegisterExtension
    static QuarkusUnitTest test = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestResource.class,TestPojo.class)
                    .addAsResource(new StringAsset(getPropertyAsString()), "application.properties")
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml"));
    
    @Test
    public void testSchema(){
        RequestSpecification request = RestAssured.given();
        request.accept(MEDIATYPE_TEXT);
        request.contentType(MEDIATYPE_TEXT);
        Response response = request.get("/graphql/schema.graphql");
        String body = response.body().asString();
        LOG.error(body);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(body.contains("\"Query root\""));
        Assertions.assertTrue(body.contains("type Query {"));
        Assertions.assertTrue(body.contains("ping: TestPojo"));
    }
    
    @Test 
    public void testBasicPost() {
        
        String pingRequest = getPayload("{\n" +
        "  ping {\n" +
        "    message\n" +
        "  }\n" +
        "}");
        
        RestAssured.given().when()
                .accept(MEDIATYPE_JSON)
                .contentType(MEDIATYPE_JSON)
                .body(pingRequest)
                .post("/graphql")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(CoreMatchers.containsString("{\"data\":{\"ping\":{\"message\":\"pong\"}}}"));
    }
    
    @Test 
    public void testSourcePost() {
        String fooRequest = getPayload("{\n" +
        "  foo {\n" +
        "    message\n" +
        "    randomNumber\n" +
        "  }\n" +
        "}");
              
        RestAssured.given().when()
                .accept(MEDIATYPE_JSON)
                .contentType(MEDIATYPE_JSON)
                .queryParam("query",fooRequest)
                .get("/graphql")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(CoreMatchers.containsString("{\"data\":{\"foo\":{\"message\":\"bar\",\"randomNumber\":\"123\"}}}"));
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