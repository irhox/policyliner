package de.tub.dima.policyliner.resources;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PolicyResourceTest {

    @Test
    void testHelloEndpoint() {
        given()
                .when().get("/policy")
                .then()
                .statusCode(200)
                .body(is("Hello Policy"));
    }
}
