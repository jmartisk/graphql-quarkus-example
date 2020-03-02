package io.quarkus.smallrye.graphql.runtime;

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
    private GraphQLConfig graphQLConfig;
    
    @Produces
    ExecutionService produceExecutionService() {
        
        LOG.error("++++++++++++++++");
        LOG.error(graphQLConfig);
        return new ExecutionService(graphQLConfig);
    }

//    @Produces
//    @DefaultBean
//    public GraphQLConfig configuration() {
//        return this.graphQLConfig;
//    }
//
//    @Produces
//    @DefaultBean
//    public GraphQLSchema graphQLSchema(){
//        return graphQLSchema;
//    }
    
    public void setSmallRyeGraphQLConfig(SmallRyeGraphQLConfig smallRyeGraphQLConfig) {
        graphQLConfig = new GraphQLConfig();
        this.graphQLConfig.setBlackList(smallRyeGraphQLConfig.exceptionsBlackList);
        this.graphQLConfig.setWhiteList(smallRyeGraphQLConfig.exceptionsWhiteList);
        this.graphQLConfig.setPrintDataFetcherException(smallRyeGraphQLConfig.printDataFetcherException);
        this.graphQLConfig.setDefaultErrorMessage(smallRyeGraphQLConfig.defaultErrorMessage);
    }
}
