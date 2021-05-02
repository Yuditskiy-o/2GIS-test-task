package ru.gis.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;

public class QueryPageTest {

    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://regions-test.2gis.com")
            .setBasePath("/1.0/regions")
            .setUrlEncodingEnabled(false)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @Nested
    class PageWithNoArgs {

        @Test
        void shouldCheckIfGetErrorWhenOnlyPage() {
            given()
                    .spec(requestSpec)
                    .queryParam("page")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page' должен быть целым числом"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageAndSign() {
            given()
                    .spec(requestSpec)
                    .queryParam("page=")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page' должен быть целым числом"));
        }
    }

    @Nested
    class PageWithIncorrectArgs {

        @Test
        void shouldCheckIfGetErrorWhenPageSignMinus1() {
            given()
                    .spec(requestSpec)
                    .queryParam("page=-1")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("page/error_schema_with_page_more_0.json"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSign0() {
            given()
                    .spec(requestSpec)
                    .queryParam("page=0")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("page/error_schema_with_page_more_0.json"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSignFloat() {
            given()
                    .spec(requestSpec)
                    .queryParam("page=1.1")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page' должен быть целым числом"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSignLetters() {
            given()
                    .spec(requestSpec)
                    .queryParam("page=акт")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page' должен быть целым числом"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSignSpecialCharacters() {
            given()
                    .spec(requestSpec)
                    .queryParam("page=$$$$")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page' должен быть целым числом"));
        }
    }

    @Nested
    class PageWithNumberFilter {

        @Test
        void shouldCheckIfSearchWorksWithPageNumberFilter() {
            given()
                    .spec(requestSpec)
                    .queryParam("page=1")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("page/page_1_schema.json"));
        }

        @Test
        void shouldCheckIfGetErrorWIthIncorrectPageNumberFilter() {
            given()
                    .spec(requestSpec)
                    .queryParam("page=20")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("error_schema_with_no_results_were_found.json"));
        }
    }
}
