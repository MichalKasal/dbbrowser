package cz.kasal.dbbrowser.container;

import cz.kasal.dbbrowser.DbbrowserApplication;
import cz.kasal.dbbrowser.restassured.RestAssuredInitListener;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;


/**
 * Base test class provides configuration for API tests
 */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(value = {
        "classpath:application.properties",
        "classpath:application-test.properties"
})
@SpringBootTest(classes = {DbbrowserApplication.class,
        DatabaseConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestExecutionListeners({RestAssuredInitListener.class})
@Testcontainers
@TestPropertySource(properties = "server.port=9091")
public abstract class BaseTestIT {

}