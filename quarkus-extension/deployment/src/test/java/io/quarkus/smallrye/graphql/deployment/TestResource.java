package io.quarkus.smallrye.graphql.deployment;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

/**
 * Just a test endpoint
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@GraphQLApi
public class TestResource {
    
    @Query
    public TestPojo ping() {
        return new TestPojo("pong");
    }
    
    @Query
    public TestPojo foo() {
        return new TestPojo("bar");
    }
    
    
    @Mutation
    public TestPojo moo(String name) {
        return new TestPojo(name);
    }
    
    public String getRandomNumber(@Source TestPojo testPojo){
        return String.valueOf(123);
    }
    
}