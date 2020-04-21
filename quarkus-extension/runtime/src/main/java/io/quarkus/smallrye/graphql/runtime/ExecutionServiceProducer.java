package io.quarkus.smallrye.graphql.runtime;

import graphql.schema.GraphQLSchema;
import io.smallrye.graphql.bootstrap.Bootstrap;
import io.smallrye.graphql.bootstrap.Config;
import io.smallrye.graphql.execution.ExecutionService;
import io.smallrye.graphql.schema.model.Schema;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.jboss.logging.Logger;

/**
 * Proxy the execution service
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@ApplicationScoped
public class ExecutionServiceProducer {
    private static final Logger LOG = Logger.getLogger(ExecutionServiceProducer.class);
    
    private Schema schema;
    private Config config;
    
    void setSchema(Schema schema){
        this.schema = schema;
    }
    
    void setConfig(Config config){
        this.config = config;
    }
    
    void initialize() {
        this.graphQLSchema = Bootstrap.bootstrap(schema);
        this.executionService = new ExecutionService(config, graphQLSchema);
    }
    
    @Produces
    ExecutionService executionService;
    
    @Produces
    GraphQLSchema graphQLSchema;
}
