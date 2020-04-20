package io.quarkus.smallrye.graphql.runtime;

import graphql.schema.GraphQLSchema;
import io.quarkus.runtime.annotations.Recorder;
import io.smallrye.graphql.execution.ExecutionService;
import io.smallrye.graphql.bootstrap.Config;
import io.smallrye.graphql.schema.model.Schema;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import javax.enterprise.inject.spi.CDI;
import org.jboss.logging.Logger;

/**
 * Recorder
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@Recorder
public class SmallRyeGraphQLRecorder {
    private static final Logger LOG = Logger.getLogger(SmallRyeGraphQLRecorder.class);
  
    public void createExecutionService(SmallRyeGraphQLConfig smallRyeGraphQLConfig,Schema schema){
        ExecutionServiceProducer executionServiceProducer = CDI.current().select(ExecutionServiceProducer.class).get();
        executionServiceProducer.setConfig(toConfig(smallRyeGraphQLConfig));
        executionServiceProducer.setSchema(schema);
        
        CDI.current().select(ExecutionService.class).get();
    }
    
    public Handler<RoutingContext> executionHandler(boolean allowGet) {
        return new SmallRyeGraphQLExecutionHandler(allowGet);     
    }
    
    public Handler<RoutingContext> schemaHandler() {
        return new SmallRyeGraphQLSchemaHandler();     
    }
    
    private Config toConfig(SmallRyeGraphQLConfig smallRyeGraphQLConfig){
        return new Config() {
            @Override
            public String getDefaultErrorMessage() {
                return smallRyeGraphQLConfig.defaultErrorMessage;
            }

            @Override
            public boolean isPrintDataFetcherException() {
                return smallRyeGraphQLConfig.printDataFetcherException;
            }

            @Override
            public List<String> getBlackList() {
                return smallRyeGraphQLConfig.exceptionsBlackList;
            }

            @Override
            public List<String> getWhiteList() {
                return smallRyeGraphQLConfig.exceptionsWhiteList;
            }

            @Override
            public boolean isAllowGet() {
                return smallRyeGraphQLConfig.allowGet;
            }

            @Override
            public boolean isMetricsEnabled() {
                return smallRyeGraphQLConfig.metricsEnabled;
            }
            
        };
    }
}
