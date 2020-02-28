package io.quarkus.smallrye.graphql.runtime;

import graphql.schema.GraphQLSchema;
import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;

/**
 * Recorder
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@Recorder
public class SmallRyeGraphQLRecorder {
    private static final Logger LOG = Logger.getLogger(SmallRyeGraphQLRecorder.class);
    
    public BeanContainerListener setSmallRyeGraphQLDetails(SmallRyeGraphQLConfig smallRyeGraphQLConfig, GraphQLSchema graphQLSchema) {
        return beanContainer -> {
            ExecutionServiceProducer producer = beanContainer.instance(ExecutionServiceProducer.class);
            producer.setSmallRyeGraphQLConfig(smallRyeGraphQLConfig);
            producer.setGraphQLSchema(graphQLSchema);
        };
    }
 
    
//    public void migrate(BeanContainer container) throws LiquibaseException {
//        Liquibase liquibase = container.instance(Liquibase.class);
//        liquibase.update(new Contexts());
//    }
    
    
    public Handler<RoutingContext> executionHandler(SmallRyeGraphQLConfig smallRyeGraphQLConfig) {
        return new SmallRyeGraphQLExecutionHandler(smallRyeGraphQLConfig);     
    }
    
    public Handler<RoutingContext> schemaHandler(){//GraphQLSchema graphQLSchema) {
        return new SmallRyeGraphQLSchemaHandler();//graphQLSchema);     
    }
    
}
