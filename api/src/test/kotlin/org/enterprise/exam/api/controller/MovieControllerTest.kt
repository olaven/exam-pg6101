package org.enterprise.exam.api.controller


import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.enterprise.exam.api.WebSecurityConfigLocalFake
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.ADMIN_USER
import org.enterprise.exam.shared.dto.MovieDTO

internal class MovieControllerTest : ControllerTestBase() {

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
                .post("/api")
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
            val movie = getDummyMovie(director.id!!)

            post(movie, ADMIN_USER)
                    .statusCode(201)
        }

        getAll()
                .statusCode(200)
                .body("data.list", iterableWithSize<MovieDTO>(n))
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

        patch(dto, ADMIN_USER)
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
    fun `PUT returns 400 on ConstraintViolation`() {

        val director = persistDirector()
        val movie = persistMovie(director)
        val dto = transformer.movieToDTO(movie)

        dto.year = 1790 //NOTE: Too old

        put(dto, ADMIN_USER)
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

    @Test
    fun `GET is paginated`() {

        persistMovies(1)
        getAll()
                .statusCode(200)
                .body("data.list", notNullValue())
                .body("data.next", nullValue()) //as only one is persisted
    }

    @Test
    fun `pagination only returns 10 per page`() {

        persistMovies(15) //NOTE: more than page size 
        getAll()
                .statusCode(200)
                .body("data.list.size()", equalTo(10))
    }

    @Test
    fun `can follow pagination next-links`() {

        persistMovies(25) //NOTE: should equal three pages with 5 on last

        val toSecondPage = getAll()
                .statusCode(200)
                .body("data.list.size()", equalTo(10))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        val toThirdPage = getAll(toSecondPage).statusCode(200)
                .body("data.list.size()", equalTo(10))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getAll(toThirdPage)
                .statusCode(200)
                .body("data.list.size()", equalTo(5))
    }

    @Test
    fun `pagination next-link is null on last page`() {

        persistMovies(15) //NOTE: more than page size
        val toSecondPage = getAll()
                .body("data.next", notNullValue())
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getAll(toSecondPage)
                .body("data.next", nullValue())
    }

    private fun delete(id: Long, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.username, user.password)
                    .delete("/api/${id}")
                    .then()

    private fun put(movie: MovieDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.username, user.password)
                    .contentType(ContentType.JSON)
                    .body(movie)
                    .put("/api/${movie.id}")
                    .then()

    private fun patch(movie: MovieDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.username, user.password)
                    .contentType("application/merge-patch+json")
                    .body(movie)
                    .patch("/api/${movie.id}")
                    .then()

    private fun post(movie: MovieDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.username, user.password)
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(movie)
                    .post("/api")
                    .then()

    private fun get(id: Long) = given()
            .accept(ContentType.JSON)
            .get("/api/${id}")
            .then()

    private fun getAll(path: String? = null) = given()
            .accept(ContentType.JSON)
            .get(path ?: "/api")
            .then()
}