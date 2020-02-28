package io.quarkus.smallrye.graphql.runtime;

import graphql.schema.GraphQLSchema;
import io.smallrye.graphql.bootstrap.SmallRyeGraphQLBootstrap;
import io.smallrye.graphql.execution.ExecutionService;
import io.smallrye.graphql.execution.GraphQLConfig;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.jboss.logging.Logger;

/**
 * Produce all the classes we need for execution
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@ApplicationScoped
public class ExecutionServiceProducer {
    private static final Logger LOG = Logger.getLogger(ExecutionServiceProducer.class);
    private SmallRyeGraphQLConfig smallRyeGraphQLConfig;
    private GraphQLSchema graphQLSchema;
    
    @Produces
    ExecutionService produceExecutionService() {
        LOG.error(">>>>>>>> producing ExecutionService !" );
        GraphQLConfig config = new GraphQLConfig();
        config.setBlackList(smallRyeGraphQLConfig.exceptionsBlackList);
        config.setWhiteList(smallRyeGraphQLConfig.exceptionsWhiteList);
        config.setPrintDataFetcherException(smallRyeGraphQLConfig.printDataFetcherException);
        config.setDefaultErrorMessage(smallRyeGraphQLConfig.defaultErrorMessage);
        
        return new ExecutionService(graphQLSchema, config);
    }

    public void setSmallRyeGraphQLConfig(SmallRyeGraphQLConfig smallRyeGraphQLConfig) {
        this.smallRyeGraphQLConfig = smallRyeGraphQLConfig;
    }
    
    public void setGraphQLSchema(GraphQLSchema graphQLSchema) {
        this.graphQLSchema = graphQLSchema;
    }
}
