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
import static org.hamcrest.Matchers.*;

public class QueryCC {

    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://regions-test.2gis.com")
            .setBasePath("/1.0/regions")
            .setUrlEncodingEnabled(false)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @Nested
    class CountryCodeWithNoArgs {

        @Test
        void shouldCheckIfSearchWorksWhenOnlyCC() {
            given()
                    .spec(requestSpec)
                    .queryParam("country_code")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("country_code/error_schema_with_country_code_only_ru_kg_kz_cz.json"));
        }

        @Test
        void shouldCheckIfSearchWorksWhenOnlyCCAndSign() {
            given()
                    .spec(requestSpec)
                    .queryParam("country_code=")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("country_code/error_schema_with_country_code_only_ru_kg_kz_cz.json"));
        }
    }

    @Nested
    class CountryCodeWithListingElementsAndArgs {

        @Test
        void shouldCheckIfGetOnlyRuCities() {
            given()
                    .spec(requestSpec)
                    .queryParam("country_code=ru")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.country.code", hasItems("ru"));
        }

        @Test
        void shouldCheckIfGetOnlyKgCities() {
            given()
                    .spec(requestSpec)
                    .queryParam("country_code=kg")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.country.code", is("kg"));
        }

        @Test
        void shouldCheckIfGetOnlyKzCities() {
            given()
                    .spec(requestSpec)
                    .queryParam("country_code=kz")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.country.code", is("kz"));
        }

        @Test
        void shouldCheckIfGetOnlyCzCities() {
            given()
                    .spec(requestSpec)
                    .queryParam("country_code=cz")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.country.code", hasItems("cz"));
        }

        @Test
        void shouldCheckIfGetErrorWithUaCities() {
            given()
                    .spec(requestSpec)
                    .queryParam("country_code=ua")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("country_code/error_schema_with_country_code_only_ru_kg_kz_cz.json"));
        }

        @Test
        void shouldCheckIfGetErrorWithIncorrectRegion() {
            given()
                    .spec(requestSpec)
                    .queryParam("country_code=fra")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("country_code/error_schema_with_country_code_only_ru_kg_kz_cz.json"));
        }

        @Test
        void shouldCheckIfGetAllRegionsByDefault() {
            given()
                    .spec(requestSpec)
                    .queryParam("page=2")
                    .queryParam("page_size=15")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.country.code", hasItems("ru", "kg", "kz", "cz"));
        }
    }
}
