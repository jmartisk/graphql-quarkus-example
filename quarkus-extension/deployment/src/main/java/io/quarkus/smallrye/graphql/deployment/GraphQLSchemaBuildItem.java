package io.quarkus.smallrye.graphql.deployment;

import graphql.schema.GraphQLSchema;
import io.quarkus.builder.item.SimpleBuildItem;

public final class GraphQLSchemaBuildItem extends SimpleBuildItem {

    private final GraphQLSchema generatedSchema;

    public GraphQLSchemaBuildItem(GraphQLSchema generatedSchema) {
        this.generatedSchema = generatedSchema;
    }

    public GraphQLSchema getGraphQLSchema() {
        return generatedSchema;
    }

}
