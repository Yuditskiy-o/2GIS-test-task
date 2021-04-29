package ru.gis.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class FirstTest {
    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://regions-test.2gis.com")
            .setBasePath("/1.0/regions")
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @Test
    void shouldCheckIfAPIWorks() {
        // Given - When - Then
        // Предусловия
        given()
                .spec(requestSpec)
                // Выполняемые действия
                .when()
                .get(basePath)
                // Проверки
                .then().assertThat().statusCode(200)
                .and().body("items.name", hasItems("Актау"));
    }
}
