package org.enterprise.exam.api.controller.remove_these

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.enterprise.exam.api.WebSecurityConfigLocalFake
import org.enterprise.exam.api.controller.ControllerTestBase
import org.enterprise.exam.shared.dto.remove_these.DirectorDTO
import org.enterprise.exam.shared.dto.remove_these.MovieDTO

internal class DirectorControllerTest : ControllerTestBase() {

    @Test
    fun `can get specific director`() {

        val director = persistDirector()
        get(director.id!!)
                .statusCode(200)
                .body("data.id", equalTo(director.id.toString()))
                .body("data.givenName", equalTo(director.givenName))
                .body("data.familyName", equalTo(director.familyName))
    }

    @Test
    fun `POST to directors returns 201 if successful`() {

        val director = getDummyDirector()

        post(director, WebSecurityConfigLocalFake.ADMIN_USER)
                .statusCode(201)
    }

    @Test
    fun `POST returns 401 if not authorized`() {

        val director = persistDirector()
        val movie = getDummyMovie(director.id!!)

        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(movie)
                .post("/api")
                .then()
                .statusCode(401)
    }

    @Test
    fun `POST to movies returns 409 if client tries to decide ID`() {

        val director = getDummyDirector()
        director.id = "20" //NOTE: not `null`

        post(director, WebSecurityConfigLocalFake.ADMIN_USER)
                .statusCode(409)
    }

    @Test
    fun `POST to movies returns 400 on constraint violations`() {

        val director = getDummyDirector()
        director.givenName = "" //NOTE: min title length is 1

        post(director, WebSecurityConfigLocalFake.ADMIN_USER)
                .statusCode(400)
    }

    @Test
    fun `POST actually creates movie in database`() {

        val n = 5;
        (0 until n).forEach {

            val director = getDummyDirector()

            post(director, WebSecurityConfigLocalFake.ADMIN_USER)
                    .statusCode(201)
        }

        getAll()
                .statusCode(200)
                .body("data.list", Matchers.iterableWithSize<MovieDTO>(n))
    }

    @Test
    fun `GET is paginated`() {

        persistDirectors(1)
        getAll()
                .statusCode(200)
                .body("data.list", Matchers.notNullValue())
                .body("data.next", Matchers.nullValue()) //as only one is persisted
    }

    @Test
    fun `pagination only returns 10 per page`() {

        persistDirectors(15) //NOTE: more than page size
        getAll()
                .statusCode(200)
                .body("data.list.size()", Matchers.equalTo(10))
    }

    @Test
    fun `can follow pagination next-links`() {

        persistDirectors(25) //NOTE: should equal three pages with 5 on last
        val toSecondPage = getAll()
                .statusCode(200)
                .body("data.list.size()", Matchers.equalTo(10))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        val toThirdPage = getAll(toSecondPage).statusCode(200)
                .body("data.list.size()", Matchers.equalTo(10))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getAll(toThirdPage)
                .statusCode(200)
                .body("data.list.size()", Matchers.equalTo(5))
    }

    @Test
    fun `pagination next-link is null on last page`() {

        persistDirectors(15) //NOTE: more than page size
        val toSecondPage = getAll()
                .body("data.next", Matchers.notNullValue())
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getAll(toSecondPage)
                .body("data.next", Matchers.nullValue())
    }

    private fun post(movie: DirectorDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.username, user.password)
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(movie)
                    .post("/directors")
                    .then()

    private fun get(id: Long) = given()
            .accept(ContentType.JSON)
            .get("/directors/${id}")
            .then()

    private fun getAll(path: String? = null) = given()
            .accept(ContentType.JSON)
            .get(path ?: "/directors")
            .then()
}