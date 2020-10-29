package cz.kasal.dbbrowser.restassured;

import cz.kasal.dbbrowser.container.BaseTestIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;


/**
 * Simple testcases for connection API
 */
class ConnectionApiTest extends BaseTestIT {


    /**
     * Test DB Connection POST request
     *
     * @throws JSONException malformed test data
     */
    @Test
    void testPostConnection() throws JSONException {
        RequestSpecification request = RestAssured.given().contentType("application/json");
        JSONObject json = new JSONObject();
        json.put("databaseName", "postgres");
        json.put("hostname", "localhost");
        json.put("name", "Test2");
        json.put("password", "password");
        json.put("port", 35432);
        json.put("username", "postgres");
        request.body(json.toString()).post("/connections")
                .then().statusCode(201).assertThat()
                .body("port", equalTo(35432)).and()
                .body("username", equalTo("postgres")).and()
                .body("id", equalTo(2));


        request = RestAssured.given().contentType("application/json");
        json = new JSONObject();
        json.put("databaseName", "postgres");
        json.put("hostname", "localhost");
        json.put("name", "Test2");
        json.put("password", "password");
        json.put("port", 35432);
        json.put("username", "postgres");
        request.body(json.toString()).post("/connections")
                .then().statusCode(409);

    }

    /**
     * Test DB Connection PUT request
     */
    @Test
    void testPutConnection() {
        RequestSpecification request = RestAssured.given().contentType("application/json");
        JSONObject json = new JSONObject();
        try {
            json.put("databaseName", "test");
            json.put("hostname", "localhost");
            json.put("name", "UpdatedTest");
            json.put("password", "password");
            json.put("port", 35432);
            json.put("username", "postgres");
            json.put("id", 1);
            request.body(json.toString()).put("/connections/1")

                    .then().statusCode(200).assertThat()
                    .body("port", equalTo(35432)).and().body("name", equalTo("UpdatedTest"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test DB Connection GET request
     */
    @Test
    void testGetConnection() {
        RestAssured.given().get("/connections/1")
                .then().statusCode(200).assertThat()
                .body("port", equalTo(35432)).and();
    }


    /**
     * Test DB Connection DELETE request
     */
    @Test
    void testDeleteConnection() {
        RestAssured.given().delete("/connections/2")
                .then().statusCode(204).assertThat();
        RestAssured.given().get("/connections/2")
                .then().statusCode(404).assertThat();
    }

    /**
     * Test Schemas GET request
     */
    @Test
    void testGetSchemas() {
        RestAssured.given().get("/connections/1/schemas")
                .then().statusCode(200).body("size()", equalTo(4));
    }

    /**
     * Test Tables GET request
     */
    @Test
    void testGetTables() {
        RestAssured.given().get("/connections/1/schemas/public/tables")
                .then().statusCode(200).body("size()", equalTo(4));
    }

    /**
     * Test Columns GET request
     */
    @Test
    void testGetColumns() {
        RestAssured.given().get("/connections/1/schemas/public/tables/person/columns")
                .then().statusCode(200).body("size()", equalTo(4));
    }

    /**
     * Test Table data preview GET request
     */
    @Test
    void testGetData() {
        RestAssured.given().get("/connections/1/schemas/public/tables/person/data")
                .then().statusCode(200).body("size()", equalTo(3));
    }

    /**
     * Test Table columns statistics GET request
     */
    @Test
    void testGetColumnStatistics() {
        RestAssured.given().get("/connections/1/schemas/public/tables/person/statistics")
                .then().statusCode(200).body("size()", equalTo(4))
                .body("[1].column", equalTo("age"))
                .body("[1].min", equalTo("20"))
                .body("[1].max", equalTo("45"))
                .body("[1].avg", equalTo("31.6666666666666667"))
                .body("[1].median", equalTo("30"));
    }

    /**
     * Test Table statistics GET request
     */
    @Test
    void testGetStatistics() {
        RestAssured.given().get("/connections/1/schemas/public/statistics")
                .then().statusCode(200).body("size()", equalTo(4));
    }
}
