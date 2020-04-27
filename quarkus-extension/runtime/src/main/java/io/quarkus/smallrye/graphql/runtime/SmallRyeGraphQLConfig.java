package io.quarkus.smallrye.graphql.runtime;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.graphql.bootstrap.Config;
import java.util.List;

/**
 * GraphQL Config
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@ConfigRoot(name = "smallrye-graphql",phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SmallRyeGraphQLConfig {

    /**
     * The rootPath under which queries will be served. Default to /graphql
     */
    @ConfigItem(defaultValue = "/graphql")
    public String rootPath;
    
    /**
     * Enable queries over HTTP GET. Default to false (POST only)
     */
    @ConfigItem(defaultValue = "false")
    public boolean allowGet;

    /**
     * List of Unchecked Exceptions that should be white-listed (Error details will flow to the client). Default is none. (All Unchecked Exceptions is hidden)
     */
    @ConfigItem(defaultValue = "[]")
    public List<String> exceptionsWhiteList;

    /**
     * List of Checked Exception that should be black-listed (Error details will not flow to the client). Default is none. (All Checked Exceptions is shown)
     */
    @ConfigItem(defaultValue = "[]")
    public List<String> exceptionsBlackList;

    /**
     * Default message for hidden exceptions.
     */
    @ConfigItem(defaultValue = "Server Error")
    public String defaultErrorMessage;

    /**
     * Enable metrics
     */
    @ConfigItem(defaultValue = "false")
    public boolean metricsEnabled;
    
    /**
     * Print detailed error messages in the log when data fetching has failed. Default to false.
     */
    @ConfigItem(defaultValue = "false")
    public boolean printDataFetcherException;
    
    
    /**
     * Include introspection types when creating the schema
     */
    @ConfigItem(defaultValue = "false")
    public boolean includeIntrospectionTypesInSchema;
    
    /**
     * Include the schema definition when creating the schema
     */
    @ConfigItem(defaultValue = "false")
    public boolean includeSchemaDefinitionInSchema;
                
    /**
     * Include directives when creating the schema
     */
    @ConfigItem(defaultValue = "false")
    public boolean includeDirectivesInSchema;
                
    /**
     * Include scalar types when creating the schema
     */
    @ConfigItem(defaultValue = "false")
    public boolean includeScalarsInSchema;
    
    /**
     * Always include the UI. By default this will only be included in dev and test.
     * Setting this to true will also include the UI in Prod
     */
    @ConfigItem(defaultValue = "false")
    public boolean alwaysIncludeUI;
    
    public Config toGraphQLConfig(){
        return new Config() {
            @Override
            public String getDefaultErrorMessage() {
                return defaultErrorMessage;
            }

            @Override
            public boolean isPrintDataFetcherException() {
                return printDataFetcherException;
            }

            @Override
            public List<String> getBlackList() {
                return exceptionsBlackList;
            }

            @Override
            public List<String> getWhiteList() {
                return exceptionsWhiteList;
            }

            @Override
            public boolean isAllowGet() {
                return allowGet;
            }

            @Override
            public boolean isMetricsEnabled() {
                return metricsEnabled;
            }

            @Override
            public boolean isIncludeIntrospectionTypesInSchema() {
                return includeIntrospectionTypesInSchema;
            }

            @Override
            public boolean isIncludeSchemaDefinitionInSchema() {
                return includeSchemaDefinitionInSchema;
            }

            @Override
            public boolean isIncludeDirectivesInSchema() {
                return includeDirectivesInSchema;
            }

            @Override
            public boolean isIncludeScalarsInSchema() {
                return includeScalarsInSchema;
            }
        };
    }
}
