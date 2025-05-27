package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.util.Endpoints;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class QueryResourceTest {

    @Test
    void testHelloEndpoint() {
        given()
                .when().get(Endpoints.QUERY_ANALYZE)
                .then()
                .statusCode(200)
                .body(is("Hello Query"));
    }
}
