package io.quarkus.smallrye.graphql.runtime;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;
import io.smallrye.graphql.schema.model.Schema;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Recorder
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@Recorder
public class SmallRyeGraphQLRecorder {
  
    public void createExecutionService(SmallRyeGraphQLConfig smallRyeGraphQLConfig,Schema schema){
        Arc.initialize();     // FIXME: why do I need this?
        ExecutionServiceProducer executionServiceProducer = Arc.container()
                .instance(ExecutionServiceProducer.class)
                .get();
        executionServiceProducer.setConfig(smallRyeGraphQLConfig.toGraphQLConfig());
        executionServiceProducer.setSchema(schema);
        executionServiceProducer.initialize();

    }
    
    public Handler<RoutingContext> executionHandler(boolean allowGet) {
        return new SmallRyeGraphQLExecutionHandler(allowGet);     
    }
    
    public Handler<RoutingContext> schemaHandler(SmallRyeGraphQLConfig smallRyeGraphQLConfig) {
        return new SmallRyeGraphQLSchemaHandler(smallRyeGraphQLConfig);     
    }
}
