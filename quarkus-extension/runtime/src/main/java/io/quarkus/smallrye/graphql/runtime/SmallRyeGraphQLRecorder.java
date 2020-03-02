package io.quarkus.smallrye.graphql.runtime;

import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import javax.enterprise.inject.spi.CDI;
import org.jboss.logging.Logger;

/**
 * Recorder
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@Recorder
public class SmallRyeGraphQLRecorder {
    private static final Logger LOG = Logger.getLogger(SmallRyeGraphQLRecorder.class);
  
    public void createExecutionServiceProducer(SmallRyeGraphQLConfig smallRyeGraphQLConfig,String graphQLSchema) {
        ExecutionServiceProducer producer = CDI.current().select(ExecutionServiceProducer.class).get();
        producer.setGraphQLSchema(graphQLSchema);
        producer.setSmallRyeGraphQLConfig(smallRyeGraphQLConfig);
        
    }
    
    public Handler<RoutingContext> executionHandler(SmallRyeGraphQLConfig smallRyeGraphQLConfig) {
        return new SmallRyeGraphQLExecutionHandler(smallRyeGraphQLConfig);     
    }
    
    public Handler<RoutingContext> schemaHandler(String graphQLSchema) {
        return new SmallRyeGraphQLSchemaHandler(graphQLSchema);     
    }
    
}
