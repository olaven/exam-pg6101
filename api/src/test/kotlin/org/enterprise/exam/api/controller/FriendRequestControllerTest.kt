package org.enterprise.exam.api.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.enterprise.exam.api.WebSecurityConfigLocalFake
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.ADMIN_USER
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.FIRST_USER
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.SECOND_USER
import org.enterprise.exam.shared.dto.FriendRequestDTO
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FriendRequestControllerTest : ControllerTestBase() {

    @BeforeEach
    fun `initialize users before sending friend requests`() {

        persistUser(FIRST_USER)
        persistUser(SECOND_USER)
    }


    @Test
    fun `POST returns 201 when successful`() {

        val dto = getFriendRequestDTO(sender = FIRST_USER, receiver = SECOND_USER)
        post(dto, FIRST_USER)
                .statusCode(201)
                .body("data.senderEmail", equalTo(dto.senderEmail))
                .body("data.receiverEmail", equalTo(dto.receiverEmail))
    }

    @Test
    fun `POST returns 400 on invalid emails`() {

        val dto = FriendRequestDTO(
                senderEmail = FIRST_USER.email,
                receiverEmail = "invalid@mail.com"
        )

        post(dto, FIRST_USER)
                .statusCode(400)
    }

    @Test
    fun `POST returns 409 if trying to decide ID`() {

        val dto = getFriendRequestDTO(sender = FIRST_USER, receiver = SECOND_USER)
        dto.id = "3434" //NOTE: not null
        post(dto, FIRST_USER)
                .statusCode(409)
    }

    @Test
    fun `POST returns 409 if trying to decide status`() {

        val dto = getFriendRequestDTO(sender = FIRST_USER, receiver = SECOND_USER)
        dto.status = FriendRequestStatus.ACCEPTED //NOTE: not null
        post(dto, FIRST_USER)
                .statusCode(409)
    }

    @Test
    fun `POST returns 403 if trying to send for someone else`() {

        val dto = getFriendRequestDTO(sender = FIRST_USER, receiver = SECOND_USER)

        //NOTE: sender is FIRST_USER
        post(dto, SECOND_USER)
                .statusCode(403)
        post(dto, ADMIN_USER)
                .statusCode(403)
        post(dto, FIRST_USER)
                .statusCode(201)
    }


    @Test
    fun `PUT returns 204 on update`() {

        val original = persistFriendRequest(SECOND_USER.email, FIRST_USER.email)
        val dto = transformer.friendRequestToDTO(original)

        assertTrue(dto.status != FriendRequestStatus.ACCEPTED)
        dto.status = FriendRequestStatus.ACCEPTED

        put(dto, FIRST_USER)
                .statusCode(204)
    }

    @Test
    fun `PUT returns 404 if it does not exist`() {

        val dto = getFriendRequestDTO(SECOND_USER, FIRST_USER)
        dto.id = "232323" //NOTE: does not exist
        put(dto, FIRST_USER)
                .statusCode(404)
    }

    @Test
    fun `PUT returns 400 on a constraint violation`() {

        val entity = persistFriendRequest(SECOND_USER.email, FIRST_USER.email)
        val dto = transformer.friendRequestToDTO(entity)

        dto.receiverEmail = "not@registered.com"
        put(dto, FIRST_USER)
                .statusCode(400)
    }

    @Test
    fun `PUT returns 403 if someone other than receiver tries to update`() {

        val original = persistFriendRequest(SECOND_USER.email, FIRST_USER.email)
        val dto = transformer.friendRequestToDTO(original)

        assertTrue(dto.status != FriendRequestStatus.ACCEPTED)
        dto.status = FriendRequestStatus.ACCEPTED

        //NOTE: FIRST_USER is receiver of this request
        put(dto, SECOND_USER)
                .statusCode(403)
        put(dto, ADMIN_USER)
                .statusCode(403)
        put(dto, FIRST_USER)
                .statusCode(204)
    }

    @Test
    fun `GET returns 200`() {

        persistFriendRequest(FIRST_USER.email, SECOND_USER.email)
        getAll(SECOND_USER.email)
                .statusCode(200)
    }


    @Test
    fun `GET returns 200 with status as well`() {

        persistFriendRequest(FIRST_USER.email, SECOND_USER.email)
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

        val toThirdPage = getWithEntirePath(toSecondPage).statusCode(200)
                .body("data.list.size()", Matchers.equalTo(10))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getWithEntirePath(toThirdPage)
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

        getWithEntirePath(toSecondPage)
                .body("data.next", Matchers.nullValue())
    }

    private fun post(firendRequestDTO: FriendRequestDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.email, user.password)
                    .contentType(ContentType.JSON)
                    .body(firendRequestDTO)
                    .post("/requests")
                    .then()

    private fun put(friendRequestDTO: FriendRequestDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.email, user.password)
                    .contentType(ContentType.JSON)
                    .body(friendRequestDTO)
                    .put("/requests/${friendRequestDTO.id}")
                    .then()

    private fun getAll(receiver: String, status: FriendRequestStatus? = null): ValidatableResponse {

        val path = if (status == null)
            "/requests?receiver=$receiver"
        else "/requests?receiver=$receiver&status=${status.name}"

        return given()
                .accept(ContentType.JSON)
                .get(path)
                .then()
    }

    fun getWithEntirePath(path: String): ValidatableResponse {

        return given()
                .accept(ContentType.JSON)
                .get(path)
                .then()
    }
}