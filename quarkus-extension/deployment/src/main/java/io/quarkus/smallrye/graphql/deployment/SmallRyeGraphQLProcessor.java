package io.quarkus.smallrye.graphql.deployment;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.smallrye.graphql.runtime.SmallRyeGraphQLConfig;
import io.quarkus.smallrye.graphql.runtime.SmallRyeGraphQLRecorder;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.HandlerType;
import io.smallrye.graphql.bootstrap.Annotations;
import io.smallrye.graphql.bootstrap.SmallRyeGraphQLBootstrap;
import io.smallrye.graphql.execution.ExecutionService;
import io.smallrye.graphql.execution.GraphQLConfig;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

/**
 * Build steps for SmallRye GraphQL
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class SmallRyeGraphQLProcessor {
    private static final String FEATURE = "smallrye-graphql";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
    BeanDefiningAnnotationBuildItem additionalBeanDefiningAnnotation() {
        // Make ArC discover the beans marked with the @GraphQlApi qualifier
        return new BeanDefiningAnnotationBuildItem(Annotations.GRAPHQL_API,DotName.createSimple(ApplicationScoped.class.getName()));
    }
    
    @BuildStep
    AdditionalBeanBuildItem additionalBean(){
        return AdditionalBeanBuildItem.builder()
                .addBeanClass(ExecutionService.class)
                .addBeanClass(GraphQLConfig.class)
                .setUnremovable().build();
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void build(CombinedIndexBuildItem combinedIndex,
            SmallRyeGraphQLRecorder recorder,
            SmallRyeGraphQLConfig smallRyeGraphQLConfig,
            BuildProducer<RouteBuildItem> routes) {
      
        IndexView index = combinedIndex.getIndex();
        
        SmallRyeGraphQLBootstrap bootstrap = new SmallRyeGraphQLBootstrap();
        GraphQLSchema graphQLSchema = bootstrap.bootstrap(index);
        
        String schema = SCHEMA_PRINTER.print(graphQLSchema);
        recorder.createExecutionService(smallRyeGraphQLConfig);
        
        Handler<RoutingContext> schemaHandler = recorder.schemaHandler(schema);
        routes.produce(new RouteBuildItem(smallRyeGraphQLConfig.rootPath + "/schema.graphql", schemaHandler, HandlerType.NORMAL));
        
        Handler<RoutingContext> executionHandler = recorder.executionHandler(smallRyeGraphQLConfig);
        routes.produce(new RouteBuildItem(smallRyeGraphQLConfig.rootPath, executionHandler, HandlerType.NORMAL));
        
    }
    
    private static final SchemaPrinter SCHEMA_PRINTER;
    static {
        SchemaPrinter.Options options = SchemaPrinter.Options.defaultOptions();
        options = options.descriptionsAsHashComments(false);
        options = options.includeDirectives(false);
        options = options.includeExtendedScalarTypes(false);
        options = options.includeIntrospectionTypes(false);
        options = options.includeScalarTypes(false);
        options = options.includeSchemaDefinition(false);
        options = options.useAstDefinitions(false);
        SCHEMA_PRINTER = new SchemaPrinter(options);
    }

}
