package io.quarkus.smallrye.graphql.deployment;

/**
 * Just a test pojo
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class TestPojo {
    String message;

    public TestPojo(){
        super();
    }

    public TestPojo(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
