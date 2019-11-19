package org.enterprise.exam.api.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.enterprise.exam.api.WebSecurityConfigLocalFake
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.FIRST_USER
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.SECOND_USER
import org.enterprise.exam.shared.dto.UserDTO
import org.enterprise.exam.shared.dto.remove_these.MovieDTO
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

internal class UserControllerTest: ControllerTestBase() {


    @Test
    fun `can retrieve user`() {

        val user = persistUser(FIRST_USER)
        get(user.id)
                .statusCode(200)
                .body("data.email", equalTo(user.email))
                .body("data.givenName", equalTo(user.givenName))
                .body("data.familyName", equalTo(user.familyName))
    }

    @Test
    fun `getting 404 if not found`() {

        get(-1)
                .statusCode(404)
                .body("code", equalTo(404))
                .body("data", nullValue())
    }


    @Test
    fun `POST returns 400 if user object is malformed`() {

        val user = getDummyUser(FIRST_USER)
        user.givenName = ""; //below minimum threshold
        post(user, FIRST_USER)
                .statusCode(400)
    }

    @Test
    fun `malformed POST returns appropriate error`() {

        val user = getDummyUser(FIRST_USER)
        user.givenName = ""; //below minimum threshold
        post(user, FIRST_USER)
                .statusCode(400)
                .body("message", equalTo("givenName size must be between 1 and 250"))
    }

    @Test
    fun `POST to movies returns 201 if successful`() {

        val user = getDummyUser(FIRST_USER)

        post(user, FIRST_USER)
                .statusCode(201)
    }

    @Test
    fun `POST returns 401 if not authorized`() {

        val user = getDummyUser(FIRST_USER)

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(401)
    }

    @Test
    fun `returns 403 if trying to create user with other email than oneself`() {

        val user = getDummyUser(FIRST_USER)

        //NOTE: posting as another (second) user
        post(user, SECOND_USER)
                .statusCode(201)
    }

    @Test
    fun `returns 409 if trying to create user with email that already exists`() {

        val firstAttempted = getDummyUser(FIRST_USER)
        post(firstAttempted, FIRST_USER)
                .statusCode(201)

        val secondAttempted = getDummyUser(FIRST_USER)
        post(secondAttempted, FIRST_USER)
                .statusCode(409)
    }

    @Test
    fun `POST to movies returns 409 if client tries to decide ID`() {

        val user = getDummyUser(FIRST_USER)
        user.email = FIRST_USER.email
        user.id = "20" //NOTE: not `null`

        post(user, FIRST_USER)
                .statusCode(409)
    }

    @Test
    fun `POST to movies returns 400 on constraint violations`() {

        val user = getDummyUser(FIRST_USER)
        user.email = FIRST_USER.email
        user.givenName  = "" //NOTE: min name  length is 1

        post(user, FIRST_USER)
                .statusCode(400)
    }

    @Test
    fun `POST actually creates movie in database`() {

        getAll()
                .statusCode(200)
                .body("data.list", Matchers.iterableWithSize<MovieDTO>(0))

        post(getDummyUser(FIRST_USER), FIRST_USER)

        getAll()
                .statusCode(200)
                .body("data.list", Matchers.iterableWithSize<MovieDTO>(1))
    }


    @Test
    fun `GET is paginated`() {

        persistUsers(1)
        getAll()
                .statusCode(200)
                .body("data.list", Matchers.notNullValue())
                .body("data.next", nullValue()) //as only one is persisted
    }

    @Test
    fun `pagination only returns 10 per page`() {

        persistUsers(15) //NOTE: more than page size
        getAll()
                .statusCode(200)
                .body("data.list.size()", equalTo(10))
    }

    @Test
    fun `can follow pagination next-links`() {

        persistUsers(25) //NOTE: should equal three pages with 5 on last

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

        persistUsers(15) //NOTE: more than page size
        val toSecondPage = getAll()
                .body("data.next", Matchers.notNullValue())
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getAll(toSecondPage)
                .body("data.next", nullValue())
    }

    private fun post(movie: UserDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.email, user.password)
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(movie)
                    .post("/users")
                    .then()

    private fun getAll(path: String? = null) = given()
            .accept(ContentType.JSON)
            .get(path ?: "/users")
            .then()

    private fun get(id: Long?) = given()
            .accept(ContentType.JSON)
            .get("/users/$id")
            .then()
}