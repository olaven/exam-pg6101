package org.olaven.enterprise.mock.movies.controller



import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.olaven.enterprise.mock.movies.WebSecurityConfigLocalFake
import org.olaven.enterprise.mock.movies.WebSecurityConfigLocalFake.Companion.ADMIN_USER
import org.olaven.enterprise.mock.movies.WebSecurityConfigLocalFake.Companion.FIRST_USER
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
    fun `GET all movies returns Wrapped responses`() {

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
        
        post(movie, ADMIN_USER)
                .statusCode(201)
    }

    @Test
    fun `POST returns 401 if not authorized`() {

        val director = persistDirector()
        val movie = getDummyMovie(director.id!!)

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(movie)
                .post("/movies")
                .then()
                .statusCode(401)
    }

    @Test
    fun `POST to movies returns 409 if client tries to decide ID`() {

        val director = persistDirector()
        val movie = getDummyMovie(director.id!!)
        movie.id = "20" //NOTE: not `null`

        post(movie, ADMIN_USER)
                .statusCode(409)
    }

    @Test
    fun `POST to movies returns 400 on constraint violations`() {

        val director = persistDirector()
        val movie = getDummyMovie(director.id!!)
        movie.title = "" //NOTE: min title length is 1

        post(movie, ADMIN_USER)
                .statusCode(400)
    }

    @Test
    fun `POST actually creates movie in database`() {

        val n = 5;
        (0 until n).forEach {

            val director = persistDirector()
            val movie =  getDummyMovie(director.id!!)

            post(movie, ADMIN_USER)
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
        patch(dto, ADMIN_USER)
                .statusCode(204)
    }

    @Test
    fun `PATCH returns actually updates in db`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        dto.title = "UPDATED TITLE"
        patch(dto, ADMIN_USER)
                .statusCode(204)

         get(movie.id!!)
            .body("data.title", equalTo(dto.title))
    }

    @Test //TODO: not sure how to handle null vaules in relevant mapping, as those would go into db and cause exception anyways
    fun `PATCH will only update what is specified in JSON merge patch`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        val originalTitle = dto.title
        val originalDirectorID = dto.directorID

        dto.title = null
        dto.directorID = null
        dto.year = 1999

        patch(dto, ADMIN_USER)
                .statusCode(204)

        // NOTE year got updated, others were ignored
        get(movie.id!!)
                .body("data.year", equalTo(1999))
                .body("data.title", equalTo(originalTitle))
                .body("data.directorID", equalTo(originalDirectorID))
    }

    @Test
    fun `PATCH returns 400 on ConstraintViolation`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        dto.year = 1799 //NOTE: movie is too old, limit is 1880

        patch(dto)
                .statusCode(400)
    }


    @Test
    fun `PUT returns 204 on successful replace`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        dto.title = "PUT-ed title"

        put(dto, ADMIN_USER)
                .statusCode(204)

        get(movie.id!!)
                .body("data.title", equalTo(dto.title))
    }

    @Test
    fun `PUT returns 400 con ConstraintViolation`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        dto.year = 1790 //NOTE: Too old

        put(dto)
                .statusCode(400)
    }


    @Test
    fun `DELETE returns 204 on successful deletion`() {

        val director = persistDirector()
        val movie = persistMovie(director)


        delete(movie.id!!, ADMIN_USER)
                .statusCode(204)
    }

    @Test
    fun `DELETE returns 404 if movie does not exist`() {

        val id = 88L

        get(id).statusCode(404) // we are sure it does not exist
        delete(id, ADMIN_USER).statusCode(404) //DELETE will not accept it either
    }

    private fun delete(id: Long, user: WebSecurityConfigLocalFake.Companion.TestUser) =
        authenticated(user.username, user.password)
            .delete("/movies/${id}")
            .then()

    private fun put(movie: MovieDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
        authenticated(user.username, user.password)
            .contentType(ContentType.JSON)
            .body(movie)
            .put("/movies/${movie.id}")
            .then()

    private fun patch(movie: MovieDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
        authenticated(user.username, user.password)
            .contentType("application/merge-patch+json")
            .body(movie)
            .patch("/movies/${movie.id}")
            .then()

    private fun post(movie: MovieDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
        authenticated(user.username, user.password)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(movie)
            .post("/movies")
            .then()

    private fun get(id: Long) = given()
            .accept(ContentType.JSON)
            .get("/movies/${id}")
            .then()

    private fun getAll()  = given()
            .accept(ContentType.JSON)
            .get("/movies")
            .then()
}