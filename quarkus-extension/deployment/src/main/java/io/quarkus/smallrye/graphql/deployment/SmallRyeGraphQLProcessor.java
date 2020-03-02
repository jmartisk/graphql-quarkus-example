package io.quarkus.smallrye.graphql.deployment;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.arc.runtime.BeanContainerListener;
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
import io.smallrye.graphql.execution.ExecutionService;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
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
    AdditionalBeanBuildItem additionalBean(){
        LOG.info("=== additional bean ===");
        return AdditionalBeanBuildItem.unremovableOf(ExecutionServiceProducer.class);
    }
    
    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void build(BeanArchiveIndexBuildItem beanArchiveIndexBuildItem,
            SmallRyeGraphQLRecorder recorder,
            SmallRyeGraphQLConfig smallRyeGraphQLConfig,
            BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer,
            BuildProducer<RouteBuildItem> routes) {
      
        
        IndexView index = beanArchiveIndexBuildItem.getIndex();
        SmallRyeGraphQLBootstrap bootstrap = new SmallRyeGraphQLBootstrap();
        GraphQLSchema graphQLSchema = bootstrap.bootstrap(index);
        String schema = schemaPrinter.print(graphQLSchema);
        
        //LOG.info("=== record bean producer ===");
        //containerListenerProducer.produce(
        //        new BeanContainerListenerBuildItem(recorder.createExecutionServiceProducer(smallRyeGraphQLConfig, schema)));
        recorder.createExecutionServiceProducer(smallRyeGraphQLConfig, schema);
        
        LOG.info("=== record routers ===");
        Handler<RoutingContext> schemaHandler = recorder.schemaHandler(schema);
        routes.produce(new RouteBuildItem(rootPath + "/schema.graphql", schemaHandler, HandlerType.NORMAL));
        
        Handler<RoutingContext> executionHandler = recorder.executionHandler(smallRyeGraphQLConfig);
        routes.produce(new RouteBuildItem(rootPath, executionHandler, HandlerType.NORMAL));
    }
    
    private final String rootPath = "/graphql"; // TODO: Move to config
    private final SchemaPrinter schemaPrinter = new SchemaPrinter();
    
    
//    @BuildStep
//    BeanDefiningAnnotationBuildItem additionalBeanDefiningAnnotation() {
//        LOG.info("=== additionalBeanDefiningAnnotation ===");
//        // Make ArC discover the beans marked with the @GraphQlApi qualifier
//        // TODO: Get the scope (if any)
//        return new BeanDefiningAnnotationBuildItem(Annotations.GRAPHQL_API,DotName.createSimple(RequestScoped.class.getName()),false);
//    }
}
