package io.quarkus.smallrye.graphql.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import io.smallrye.graphql.schema.model.Schema;

/**
 * Hold the generated Schema
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public final class GraphQLSchemaBuildItem extends SimpleBuildItem {
    private final Schema schema;
    
    public GraphQLSchemaBuildItem(Schema schema){
        this.schema = schema;
    }
    
    public Schema getSchema(){
        return this.schema;
    }
}
