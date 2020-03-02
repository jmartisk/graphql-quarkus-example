package io.quarkus.smallrye.graphql.runtime;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import java.util.List;

@ConfigRoot(name = "smallrye-graphql",phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class SmallRyeGraphQLConfig {

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
     * Print detailed error messages in the log when data fetching has failed. Default to false.
     */
    @ConfigItem(defaultValue = "false")
    public boolean printDataFetcherException;
    
}