package org.olaven.enterprise.mock.movies.controller



import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.olaven.enterprise.mock.movies.dto.MovieDTO

internal class MovieControllerTest: ControllerTestBase() {

    @Test
    fun `getting all movies returns 200`() {

        val n = 10;
        persistMovies(n)

        getAll()
                .statusCode(200)
    }

    @Test
    fun `getting all movies returns Wrapped responses`() {

        persistMovies(1)
        getAll()
                .statusCode(200)
                .body("data", notNullValue())
                .body("status", notNullValue())
    }

    @Test
    fun `POST to movies returns 201 if successful`() {

        val director = persistDirector()
        val movie = getDummyMovie(director.id!!)
        
        post(movie)
                .statusCode(201)
    }

    private fun post(movie: MovieDTO) = given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(movie)
            .post("/movies")
            .then()

    private fun getAll()  = given().accept(ContentType.JSON)
            .get("/movies")
            .then()
}