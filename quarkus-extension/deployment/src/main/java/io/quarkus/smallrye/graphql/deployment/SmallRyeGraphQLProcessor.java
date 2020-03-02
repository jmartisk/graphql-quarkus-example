package io.quarkus.smallrye.graphql.deployment;

import graphql.schema.GraphQLSchema;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.test.TestResourceProvider;
import io.quarkus.smallrye.graphql.runtime.ExecutionServiceProducer;
import io.quarkus.smallrye.graphql.runtime.SmallRyeGraphQLConfig;
import io.quarkus.smallrye.graphql.runtime.SmallRyeGraphQLRecorder;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.HandlerType;
import io.smallrye.graphql.bootstrap.Annotations;
import io.smallrye.graphql.bootstrap.SmallRyeGraphQLBootstrap;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

/**
 * Build steps for SmallRye GraphQL
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class SmallRyeGraphQLProcessor {
    private static final Logger LOG = Logger.getLogger(SmallRyeGraphQLProcessor.class);
    
    private static final String FEATURE = "smallrye-graphql";

    @BuildStep
    FeatureBuildItem feature() {
        LOG.info("=== feature ===");
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
    BeanDefiningAnnotationBuildItem additionalBeanDefiningAnnotation() {
        LOG.info("=== additionalBeanDefiningAnnotation ===");
        // Make ArC discover the beans marked with the @GraphQlApi qualifier
        // TODO: Get the scope (if any)
        return new BeanDefiningAnnotationBuildItem(Annotations.GRAPHQL_API,DotName.createSimple(RequestScoped.class.getName()),false);
    }
    
    @BuildStep
    List<AdditionalBeanBuildItem> additionalBean(){
        LOG.info("=== additional bean ===");
        List<AdditionalBeanBuildItem> additionalBeans = new ArrayList<>();
        additionalBeans.add(AdditionalBeanBuildItem.unremovableOf(ExecutionServiceProducer.class));
//        additionalBeans.add(AdditionalBeanBuildItem.unremovableOf("io.quarkus.smallrye.graphql.deployment.TestResource"));
        return additionalBeans;
    }
    
    @BuildStep
    GraphQLSchemaBuildItem index(BeanArchiveIndexBuildItem beanArchiveIndexBuildItem) {
        LOG.info("=== index ===");
        IndexView index = beanArchiveIndexBuildItem.getIndex();
        SmallRyeGraphQLBootstrap bootstrap = new SmallRyeGraphQLBootstrap();
        GraphQLSchema graphQLSchema = bootstrap.bootstrap(index);
        return new GraphQLSchemaBuildItem(graphQLSchema);
    }
    
    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void build(GraphQLSchemaBuildItem graphQLSchemaBuildItem,
            SmallRyeGraphQLRecorder recorder,
            SmallRyeGraphQLConfig smallRyeGraphQLConfig,
            BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer,
            BuildProducer<RouteBuildItem> routes) {
      
        String schema = graphQLSchemaBuildItem.getGraphQLSchemaAsString();
        recorder.createExecutionServiceProducer(smallRyeGraphQLConfig);
        
//        GraphQLSchema graphQLSchema = graphQLSchemaBuildItem.getGraphQLSchema();
        
//        LOG.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> allTypes");
//        List<GraphQLNamedType> allTypes = graphQLSchema.getAllTypesAsList();
//        for(GraphQLNamedType graphQLType: allTypes){
//            LOG.error(graphQLType);
//        }
//        
//        LOG.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> codeRegistry");
//        GraphQLCodeRegistry codeRegistry = graphQLSchema.getCodeRegistry();
//        LOG.error(codeRegistry.);
        
        
        
        LOG.info("=== record routers ===");
        Handler<RoutingContext> schemaHandler = recorder.schemaHandler(schema);
        routes.produce(new RouteBuildItem(rootPath + "/schema.graphql", schemaHandler, HandlerType.NORMAL));
        
        Handler<RoutingContext> executionHandler = recorder.executionHandler(smallRyeGraphQLConfig);
        routes.produce(new RouteBuildItem(rootPath, executionHandler, HandlerType.NORMAL));
    }
    
    private final String rootPath = "/graphql"; // TODO: Move to config
    

}
