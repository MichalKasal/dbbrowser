package cz.kasal.dbbrowser.restassured;

import io.restassured.RestAssured;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * Provides RestAssured configuration such as port where test server runs and hostname
 */
public class RestAssuredInitListener implements TestExecutionListener {

    @Override
    public void prepareTestInstance(TestContext testContext) {
        String port = testContext.getApplicationContext().getEnvironment().getProperty("server.port");
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Integer.parseInt(port);
        RestAssured.basePath = "/";
    }
}
