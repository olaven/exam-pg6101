package org.olaven.enterprise.mock.movies.controller

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.olaven.enterprise.mock.movies.WebSecurityConfigLocalFake
import org.olaven.enterprise.mock.movies.dto.DirectorDTO
import org.olaven.enterprise.mock.movies.dto.MovieDTO

internal class DirectorControllerTest: ControllerTestBase() {

    @Test
    fun `POST to movies returns 201 if successful`() {

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
                .post("/movies")
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
                .body("data", Matchers.iterableWithSize<MovieDTO>(n))
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

    private fun getAll()  = given()
            .accept(ContentType.JSON)
            .get("/directors")
            .then()
}