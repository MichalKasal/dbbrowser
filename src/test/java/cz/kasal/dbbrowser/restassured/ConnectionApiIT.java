package cz.kasal.dbbrowser.restassured;

import cz.kasal.dbbrowser.container.BaseTestIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class ConnectionApiIT extends BaseTestIT {

    @Test
    public void testPostConnection() {
        RequestSpecification request = RestAssured.given().contentType("application/json");
        JSONObject json = new JSONObject();
        try {
            json.put("databaseName", "postgres");
            json.put("hostname", "localhost");
            json.put("name", "Test2");
            json.put("password", "password");
            json.put("port", 5432);
            json.put("username", "postgres");
            request.body(json.toString()).post("/connections")

                    .then().statusCode(201).assertThat()
                    .body("port", equalTo(5432)).and().body("username", equalTo("postgres")).body("id", equalTo(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutConnection() {
        RequestSpecification request = RestAssured.given().contentType("application/json");
        JSONObject json = new JSONObject();
        try {
            json.put("databaseName", "postgres");
            json.put("hostname", "localhost");
            json.put("name", "Test");
            json.put("password", "password");
            json.put("port", 15432);
            json.put("username", "admin");
            json.put("id", 1);
            request.body(json.toString()).put("/connections/1")

                    .then().statusCode(200).assertThat()
                    .body("port", equalTo(15432)).and().body("username", equalTo("admin"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetConnection() {
        RestAssured.given().get("/connections/1")
                .then().statusCode(200).assertThat()
                .body("port", equalTo(15432)).and().body("username", equalTo("admin"));
    }


    @Test
    public void testDeleteConnection() {
        RestAssured.given().delete("/connections/2")
                .then().statusCode(204).assertThat();
        RestAssured.given().get("/connections/2")
                .then().statusCode(404).assertThat();
    }


}
