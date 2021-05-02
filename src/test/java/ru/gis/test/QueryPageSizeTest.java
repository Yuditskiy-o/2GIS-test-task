package ru.gis.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class QueryPageSizeTest {

    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://regions-test.2gis.com")
            .setBasePath("/1.0/regions")
            .setUrlEncodingEnabled(false)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @Nested
    class PageSizeWithNoArgs {

        @Test
        void shouldCheckIfGetErrorWhenOnlyPageSize() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page_size' должен быть целым числом"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSizeAndSign() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page_size' должен быть целым числом"));
        }
    }

    @Nested
    class PageSizeWithIncorrectArgs {

        @Test
        void shouldCheckIfGetErrorWhenPageSizeSignMinus1() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=-1")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page_size' может быть одним из следующих значений: 5, 10, 15"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSizeSign0() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=0")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page_size' может быть одним из следующих значений: 5, 10, 15"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSizeSignFloat() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=1.1")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page_size' может быть одним из следующих значений: 5, 10, 15"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSizeSignLetters() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=акт")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page_size' может быть одним из следующих значений: 5, 10, 15"));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSizeSignSpecialCharacters() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=$$$$")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page_size' может быть одним из следующих значений: 5, 10, 15"));
        }
    }

    @Nested
    class PageSizeWithSizeFilterAndDefaultCountTest {

        @Test
        void shouldCheckIfSearchWorksWithExact5Cities() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=5")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items", hasSize(5));
        }

        @Test
        void shouldCheckIfSearchWorksWithExact10Cities() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=10")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items", hasSize(10));
        }

        @Test
        void shouldCheckIfSearchWorksWithExact15Cities() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=15")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items", hasSize(15));
        }

        @Test
        void shouldCheckIfGetErrorWhenPageSizeMoreWhen15() {
            given()
                    .spec(requestSpec)
                    .queryParam("page_size=20")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("error.message", equalTo("Параметр 'page_size' может быть одним из следующих значений: 5, 10, 15"));
        }

        @Test
        void shouldCheckIfDefaultCountOfRegionsIs15() {
            given()
                    .spec(requestSpec)
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items", hasSize(15));
        }
    }
}
