package io.quarkus.smallrye.graphql.runtime;

import io.quarkus.runtime.annotations.Recorder;
import io.smallrye.graphql.execution.ExecutionService;
import io.smallrye.graphql.execution.GraphQLConfig;
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
  
    public void createExecutionService(SmallRyeGraphQLConfig smallRyeGraphQLConfig) {
        GraphQLConfig graphQLConfig = CDI.current().select(GraphQLConfig.class).get();
        // TODO: Only set if the value is not default.
        graphQLConfig.setBlackList(smallRyeGraphQLConfig.exceptionsBlackList);
        graphQLConfig.setWhiteList(smallRyeGraphQLConfig.exceptionsWhiteList);
        graphQLConfig.setPrintDataFetcherException(smallRyeGraphQLConfig.printDataFetcherException);
        graphQLConfig.setDefaultErrorMessage(smallRyeGraphQLConfig.defaultErrorMessage);

        CDI.current().select(ExecutionService.class).get();

    }
    
    public Handler<RoutingContext> executionHandler(SmallRyeGraphQLConfig smallRyeGraphQLConfig) {
        return new SmallRyeGraphQLExecutionHandler(smallRyeGraphQLConfig);     
    }
    
    public Handler<RoutingContext> schemaHandler(String graphQLSchema) {
        return new SmallRyeGraphQLSchemaHandler(graphQLSchema);     
    }
}
