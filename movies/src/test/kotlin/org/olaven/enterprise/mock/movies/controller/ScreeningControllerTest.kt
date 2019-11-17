package org.olaven.enterprise.mock.movies.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ScreeningControllerTest : ControllerTestBase() {

    @Test
    fun `A screening has has no tickets sold by default`() {

        val screening = persistScreening()
        get(screening.id!!)
                .statusCode(200)
                .body("data.availableTickets", equalTo(0))
    }

    @Test
    fun `can GET specific screening`() {

        val screening = persistScreening()
        get(screening.id!!)
                .statusCode(200)
                .body("data.room", equalTo(screening.room.toString()))
                .body("data.movieID", equalTo(screening.movie.id.toString()))
    }

    @Test
    fun `GET collection is paginated`() {

        persistScreenings(1)
        getAll()
                .statusCode(200)
                .body("data.list", Matchers.notNullValue())
                .body("data.next", Matchers.nullValue()) //as only one is persisted
    }

    @Test
    fun `pagination only returns 10 per page`() {

        persistScreenings(25) //NOTE: more than page size
        getAll()
                .statusCode(200)
                .body("data.list.size()", Matchers.equalTo(20))
    }

    @Test
    fun `can follow pagination next-links`() {

        persistScreenings(45) //NOTE: should equal three pages with 5 on last

        val toSecondPage = getAll()
                .statusCode(200)
                .body("data.list.size()", Matchers.equalTo(20))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        val toThirdPage = getAll(toSecondPage).statusCode(200)
                .body("data.list.size()", Matchers.equalTo(20))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getAll(toThirdPage)
                .statusCode(200)
                .body("data.list.size()", Matchers.equalTo(5))
    }

    @Test
    fun `pagination next-link is null on last page`() {

        persistScreenings(25) //NOTE: more than page size
        val toSecondPage = getAll()
                .body("data.next", Matchers.notNullValue())
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getAll(toSecondPage)
                .body("data.next", Matchers.nullValue())
    }

    private fun get(id: Long) = given()
            .contentType(ContentType.JSON)
            .get("/screenings/$id")
            .then()

    private fun getAll(path: String? = null) = given()
            .contentType(ContentType.JSON)
            .get(path ?: "/screenings")
            .then()
}