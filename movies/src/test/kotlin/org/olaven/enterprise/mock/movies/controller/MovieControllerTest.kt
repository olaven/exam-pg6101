package org.olaven.enterprise.mock.movies.controller



import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Disabled
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

    @Test
    fun `POST to movies returns 409 if client tries to decide ID`() {

        val director = persistDirector()
        val movie = getDummyMovie(director.id!!)
        movie.id = "20" //NOTE: not `null`

        post(movie)
                .statusCode(409)
    }

    @Test
    fun `POST to movies returns 400 on constraint violations`() {

        val director = persistDirector()
        val movie = getDummyMovie(director.id!!)
        movie.title = "" //NOTE: min title length is 1

        post(movie)
                .statusCode(400)
    }

    @Test
    fun `POST actually creates movie in database`() {

        val n = 5;
        (0 until n).forEach {

            val director = persistDirector()
            val movie =  getDummyMovie(director.id!!)

            post(movie)
                    .statusCode(201)
        }

        getAll()
                .statusCode(200)
                .body("data", iterableWithSize<MovieDTO>(n))
    }

    @Test
    fun `PATCH returns 204 on successful update`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        dto.title = "UPDATED TITLE"
        patch(dto)
                .statusCode(204)
    }

    @Test
    fun `PATCH returns actually updates in db`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        dto.title = "UPDATED TITLE"
        patch(dto)
                .statusCode(204)

         get(movie.id!!)
            .body("data.title", equalTo(dto.title))
    }

    @Test @Disabled //not sure how to handle null vaules in relevant mapping, as those would go into db and cause exception anyways
    fun `PATCH will only update what is specified in JSON merge patch`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        dto.title = null
        dto.id = null
        dto.directorID = null
        dto.year = 1999

        patch(dto)
                .statusCode(204)

        get(movie.id!!)
                .body("data.year", equalTo(1999))
    }


    @Test
    fun `PUT returns 204 on successful replace`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        dto.title = "PUT-ed title"

        put(dto)
                .statusCode(204)

        get(movie.id!!) //TODO: for some reason getting 200
                .body("data.code", equalTo(204))
    }

    private fun put(movie: MovieDTO) = given()
            .contentType(ContentType.JSON)
            .body(movie)
            .put("/movies/${movie.id}")
            .then()

    private fun patch(movie: MovieDTO) = given()
            .contentType("application/merge-patch+json")
            .body(movie)
            .patch("/movies/${movie.id}")
            .then()

    private fun post(movie: MovieDTO) = given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(movie)
            .post("/movies")
            .then()

    private fun get(id: Long) = given().accept(ContentType.JSON)
            .get("/movies/${id}")
            .then()

    private fun getAll()  = given().accept(ContentType.JSON)
            .get("/movies")
            .then()
}