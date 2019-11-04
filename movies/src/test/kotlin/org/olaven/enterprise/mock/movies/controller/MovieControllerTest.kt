package org.olaven.enterprise.mock.movies.controller



import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class MovieControllerTest: ControllerTestBase() {

    @Test
    fun `getting all movies returns 200`() {

        val n = 10;
        persistMovies(n)

        given().accept(ContentType.JSON)
                .get("/movies")
                .then()
                .statusCode(200)
    }
}