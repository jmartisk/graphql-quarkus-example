package io.quarkus.smallrye.graphql.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.smallrye.graphql.runtime.ExecutionServiceProducer;
import io.quarkus.smallrye.graphql.runtime.SmallRyeGraphQLConfig;
import io.quarkus.smallrye.graphql.runtime.SmallRyeGraphQLRecorder;
import io.quarkus.vertx.http.deployment.RequireBodyHandlerBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.HandlerType;
import io.smallrye.graphql.schema.Annotations;
import io.smallrye.graphql.schema.SchemaBuilder;
import io.smallrye.graphql.schema.model.Schema;
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
                .addBeanClass(ExecutionServiceProducer.class)
                .setUnremovable().build();
    }
    
    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    void executionService(CombinedIndexBuildItem combinedIndex,
                        SmallRyeGraphQLRecorder recorder,
                        SmallRyeGraphQLConfig smallRyeGraphQLConfig){
    
        IndexView index = combinedIndex.getIndex();
        Schema schema = SchemaBuilder.build(index);
        
        recorder.createExecutionService(smallRyeGraphQLConfig, schema);
    }
            
    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    RequireBodyHandlerBuildItem build(BuildProducer<RouteBuildItem> routes,
                                    SmallRyeGraphQLRecorder recorder,
                                    SmallRyeGraphQLConfig smallRyeGraphQLConfig) {

        Handler<RoutingContext> executionHandler = recorder.executionHandler(smallRyeGraphQLConfig.allowGet);
        routes.produce(new RouteBuildItem(smallRyeGraphQLConfig.rootPath, executionHandler, HandlerType.BLOCKING,true));
        
        Handler<RoutingContext> schemaHandler = recorder.schemaHandler(smallRyeGraphQLConfig);
        routes.produce(new RouteBuildItem(smallRyeGraphQLConfig.rootPath + "/schema.graphql", schemaHandler, HandlerType.BLOCKING,true));

        return new RequireBodyHandlerBuildItem();
    }
    
}
