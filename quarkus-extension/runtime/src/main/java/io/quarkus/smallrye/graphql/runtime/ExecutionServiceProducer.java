package io.quarkus.smallrye.graphql.runtime;

import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
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
    private GraphQLSchema graphQLSchema;
    
    @Produces
    ExecutionService produceExecutionService() {
        
        LOG.error("++++++++++++++++");
        LOG.error(graphQLSchema);
        LOG.error(graphQLConfig);
        return new ExecutionService(graphQLSchema, graphQLConfig);
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
    
    public void setGraphQLSchema(String schema) {
        this.graphQLSchema = fromString(schema);
    }
    
    private GraphQLSchema fromString(String schemaString){
        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        TypeDefinitionRegistry typeRegistry = schemaParser.parse(schemaString);
        RuntimeWiring wiring = buildRuntimeWiring();
        return schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
    }
    
    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                // this uses builder function lambda syntax
                .type("QueryType", typeWiring -> typeWiring
                        .dataFetcher("ping", new StaticDataFetcher("pong"))
                )
                .build();
    }
    
}
