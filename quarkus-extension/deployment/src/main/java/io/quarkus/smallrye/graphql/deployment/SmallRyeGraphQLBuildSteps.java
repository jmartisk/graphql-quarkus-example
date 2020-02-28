package io.quarkus.smallrye.graphql.deployment;

import graphql.schema.GraphQLSchema;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.smallrye.graphql.runtime.ExecutionServiceProducer;
import io.quarkus.smallrye.graphql.runtime.SmallRyeGraphQLConfig;
import io.quarkus.smallrye.graphql.runtime.SmallRyeGraphQLRecorder;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.HandlerType;
import io.smallrye.graphql.bootstrap.SmallRyeGraphQLBootstrap;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

/**
 * Build steps for SmallRye GraphQL
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class SmallRyeGraphQLBuildSteps {
    private static final Logger LOG = Logger.getLogger(SmallRyeGraphQLBuildSteps.class);
    
    @BuildStep
    FeatureBuildItem feature() {
        LOG.info("=== feature ===");
        return new FeatureBuildItem("smallrye-graphql");
    }
//    
//    @BuildStep
//    BeanDefiningAnnotationBuildItem additionalBeanDefiningAnnotation() {
//        LOG.info("=== additionalBeanDefiningAnnotation ===");
//        // Make ArC discover the beans marked with the @GraphQlApi qualifier
//        // TODO: Get the scope (if any)
//        return new BeanDefiningAnnotationBuildItem(Annotations.GRAPHQL_API,DotName.createSimple(RequestScoped.class.getName()),false);
//    }
//    
//    @BuildStep
//    void additionalBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {        
//       
//        LOG.info("=== additionalBeans ===");
//        AdditionalBeanBuildItem beanBuildItem = AdditionalBeanBuildItem.unremovableOf(ExecutionServiceProducer.class);
//        additionalBeanProducer.produce(beanBuildItem);
//    }

    @BuildStep
    void index(BeanArchiveIndexBuildItem beanArchive,BuildProducer<GraphQLSchemaBuildItem> buildProducer) {
        LOG.info("=== index ===");
        
        IndexView index = beanArchive.getIndex();
        SmallRyeGraphQLBootstrap bootstrap = new SmallRyeGraphQLBootstrap();
        GraphQLSchema graphQLSchema = bootstrap.bootstrap(index);
        
        buildProducer.produce(new GraphQLSchemaBuildItem(graphQLSchema));
    }
//    
//    @BuildStep
//    @Record(ExecutionTime.STATIC_INIT)
//    void registerVertxEndpoints(SmallRyeGraphQLRecorder recorder,
//            SmallRyeGraphQLConfig config,
//            BuildProducer<RouteBuildItem> routes,
//            GraphQLSchemaBuildItem buildItem) {
//
//        LOG.info("=== registerVertxEndpoints ===");
//        
//        final String rootPath = "/graphql"; // TODO: Move to config
//        
//        GraphQLSchema graphQLSchema = buildItem.getGraphQLSchema();
//        
//        Handler<RoutingContext> schemaHandler = recorder.schemaHandler(graphQLSchema);
//        routes.produce(new RouteBuildItem(rootPath + "/schema.graphql", schemaHandler, HandlerType.NORMAL));
//        
//        Handler<RoutingContext> executionHandler = recorder.executionHandler(config);
//        routes.produce(new RouteBuildItem(rootPath, executionHandler, HandlerType.NORMAL));
//                
//    }
    
    private SmallRyeGraphQLConfig smallRyeGraphQLConfig;
    
    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    void build(
            SmallRyeGraphQLRecorder recorder,
            BuildProducer<RouteBuildItem> routes,
            BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer,
            GraphQLSchemaBuildItem buildItem) {

        LOG.info("=== record ===");
        
        GraphQLSchema graphQLSchema = buildItem.getGraphQLSchema();
        
        //containerListenerProducer.produce(new BeanContainerListenerBuildItem(recorder.setSmallRyeGraphQLDetails(smallRyeGraphQLConfig, graphQLSchema)));
        
        Handler<RoutingContext> schemaHandler = recorder.schemaHandler();
        routes.produce(new RouteBuildItem(rootPath + "/schema.graphql", schemaHandler, HandlerType.NORMAL));
        
        Handler<RoutingContext> executionHandler = recorder.executionHandler(smallRyeGraphQLConfig);
        routes.produce(new RouteBuildItem(rootPath, executionHandler, HandlerType.NORMAL));
    }
    
    private final String rootPath = "/graphql"; // TODO: Move to config
}
