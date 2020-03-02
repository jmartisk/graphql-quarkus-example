package io.quarkus.smallrye.graphql.deployment;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import io.quarkus.builder.item.SimpleBuildItem;

public final class GraphQLSchemaBuildItem extends SimpleBuildItem {

    private final GraphQLSchema graphQLSchema;

    public GraphQLSchemaBuildItem(GraphQLSchema graphQLSchema) {
        this.graphQLSchema = graphQLSchema;
    }

    public GraphQLSchema getGraphQLSchema() {
        return graphQLSchema;
    }
    
    public String getGraphQLSchemaAsString() {
        return schemaPrinter.print(graphQLSchema);
    }

    private static SchemaPrinter schemaPrinter;
    static {
        SchemaPrinter.Options options = SchemaPrinter.Options.defaultOptions();
        options = options.descriptionsAsHashComments(false);
        options = options.includeDirectives(false);
        options = options.includeExtendedScalarTypes(false);
        options = options.includeIntrospectionTypes(false);
        options = options.includeScalarTypes(false);
        options = options.includeSchemaDefinition(false);
        options = options.useAstDefinitions(false);
        schemaPrinter = new SchemaPrinter(options);
    }
}
