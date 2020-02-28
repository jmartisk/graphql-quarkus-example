package io.quarkus.smallrye.graphql.deployment;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

/**
 * Just a test endpoint
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@ApplicationScoped
@GraphQLApi
public class TestResource {
    @Query
    public TestPojo ping() {
            return new TestPojo("ping");
        }
    }