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
import static org.hamcrest.Matchers.hasItems;

public class QueryQTest {
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
        void shouldCheckIfGetErrorWhereQIs3IncorrectSymbols() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=пол")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("error_schema_with_no_results_were_found.json"));
        }
    }

    @Nested
    class SuccessfulAndUnsuccessfulSearchForFullNames {

        @Test
        void shouldCheckIfSearchWorksWithCorrectFullName() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=Новосибирск")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.name", hasItems("Новосибирск"));
        }

        @Test
        void shouldCheckIfGetErrorWhereQIsIncorrectFullName() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=Ставрополь")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("error_schema_with_no_results_were_found.json"));
        }

        @Test
        void shouldCheckIfGetErrorWhereQIsFullNameWith2Symbols() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=Ош")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("q/error_schema_with_q_below_3.json"));
        }
    }

    @Nested
    class QWith30andMoreSymbols {

        @Test
        void shouldCheckIfGetErrorWhereQIs30Symbols() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=Новосибирсккккккккккккккккккк")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("error_schema_with_no_results_were_found.json"));
        }

        @Test
        void shouldCheckIfGetErrorWhereQIs31Symbols() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=Новосибирскккккккккккккккккккф")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("q/error_schema_with_q_more_30.json"));
        }

        @Test
        void shouldCheckIfGetErrorWhereQIs50Symbols() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=Новосибирскккккккккккккккккккфыыыыыыыыыыффффффффф")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body(matchesJsonSchemaInClasspath("q/error_schema_with_q_more_30.json"));
        }
    }

    @Nested
    class LowercaseAndUppercaseChecks {

        @Test
        void shouldCheckIfSearchWorksWithCorrectFullNameInUpperCase() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=НОВОСИБИРСК")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.name", hasItems("Новосибирск"));
        }

        @Test
        void shouldCheckIfSearchWorksWithCorrectFullNameInLowerCase() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=новосибирск")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.name", hasItems("Новосибирск"));
        }
    }

    @Nested
    class SearchWithQAndOtherQuery {

        @Test
        void shouldCheckIfSearchWorksWithQIs3SymbolsAndOtherQuery() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=рск")
                    .queryParam("сountry_code=ru")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.name", hasItems("Красноярск", "Магнитогорск", "Новосибирск", "Орск", "Усть-Каменогорск"));
        }

        @Test
        void shouldCheckIfSearchWorksWithCorrectFullNameInLowerCase() {
            given()
                    .spec(requestSpec)
                    .queryParam("q=рск")
                    .queryParam("page=2")
                    // Выполняемые действия
                    .when()
                    .get(basePath)
                    // Проверки
                    .then().assertThat().statusCode(200)
                    .and().body("items.name", hasItems("Красноярск", "Магнитогорск", "Новосибирск", "Орск", "Усть-Каменогорск"));
        }
    }
}
