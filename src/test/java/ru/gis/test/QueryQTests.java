package ru.gis.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class QueryQTests {
    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://regions-test.2gis.com")
            .setBasePath("/1.0/regions")
            .setUrlEncodingEnabled(false)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @Nested
    class QBelow3Tests {

        @Test
        void shouldCheckIfGetErrorWithOnlyQ() {
            // Given - When - Then
            // Предусловия
            given()
                    .spec(requestSpec)
                    .queryParam("q")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("q/error_schema_with_q_below_3.json"));
        }

        @Test
        void shouldCheckIfGetErrorWithQAndSign() {
            // Given - When - Then
            // Предусловия
            given()
                    .spec(requestSpec)
                    .queryParam("q=")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("q/error_schema_with_q_below_3.json"));
        }

        @Test
        void shouldCheckIfGetErrorWithQAnd1Symbol() {
            // Given - When - Then
            // Предусловия
            given()
                    .spec(requestSpec)
                    .queryParam("q=а")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("q/error_schema_with_q_below_3.json"));
        }

        @Test
        void shouldCheckIfGetErrorWithQAnd2Symbols() {
            // Given - When - Then
            // Предусловия
            given()
                    .spec(requestSpec)
                    .queryParam("q=ак")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("q/error_schema_with_q_below_3.json"));
        }
    }

    @Nested
    class SuccessfulAndUnsuccessfulSearchForQSign3 {

        @Test
        void shouldCheckIfSearchWorksWith3CorrectSymbols() {
            // Given - When - Then
            // Предусловия
            given()
                    .spec(requestSpec)
                    .queryParam("q=акт")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.name", hasItems("Актау", "Актобе"));
        }

        @Test
        void shouldCheckIfSearchWorksWith3IncorrectSymbols() {
            // Given - When - Then
            // Предусловия
            given()
                    .spec(requestSpec)
                    .queryParam("q=пол")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("q/error_schema_with_no_results_were_found.json"));
        }
    }
}
