package org.enterprise.exam.api.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.FIRST_USER
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.SECOND_USER
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FriendRequestControllerTest : ControllerTestBase() {

    @BeforeEach
    fun `initialize users before sending friend requests`() {

        persistUser(FIRST_USER)
        persistUser(SECOND_USER)
    }

    @Test
    fun `GET returns 200`() {

        persistFriendRequest(FIRST_USER, SECOND_USER)
        getAll(SECOND_USER.email)
                .statusCode(200)
    }


    @Test
    fun `GET returns 200 with status as well`() {

        persistFriendRequest(FIRST_USER, SECOND_USER)
        getAll(SECOND_USER.email, FriendRequestStatus.PENDING)
                .statusCode(200)
    }


    @Test
    fun `GET is paginated`() {

        persistFriendRequests(1, FIRST_USER, SECOND_USER)
        getAll(SECOND_USER.email)
                .statusCode(200)
                .body("data.list", Matchers.notNullValue())
                .body("data.next", Matchers.nullValue()) //as only one is persisted
    }

    @Test
    fun `pagination only returns 10 per page`() {

        persistFriendRequests(15, FIRST_USER, SECOND_USER) //NOTE: more than page size
        getAll(SECOND_USER.email)
                .statusCode(200)
                .body("data.list.size()", Matchers.equalTo(10))
    }

    @Test
    fun `can follow pagination next-links`() {

        persistFriendRequests(25, FIRST_USER, SECOND_USER) //NOTE: should equal three pages with 5 on last

        val toSecondPage = getAll(SECOND_USER.email)
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

        persistFriendRequests(15, FIRST_USER, SECOND_USER) //NOTE: more than page size
        val toSecondPage = getAll(SECOND_USER.email)
                .body("data.next", Matchers.notNullValue())
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getAll(toSecondPage)
                .body("data.next", Matchers.nullValue())
    }


    private fun getAll(receiver: String, status: FriendRequestStatus? = null): ValidatableResponse {

        val path = if (status == null)
            "/requests?receiver=$receiver"
        else "/requests?receiver=$receiver&status=${status.name}"

        return given()
                .accept(ContentType.JSON)
                .get(path)
                .then()
    }
}