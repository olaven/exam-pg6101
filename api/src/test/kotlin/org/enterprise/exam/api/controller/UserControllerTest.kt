package org.enterprise.exam.api.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.enterprise.exam.api.WebSecurityConfigLocalFake
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.FIRST_USER
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.SECOND_USER
import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.enterprise.exam.shared.dto.UserDTO
import org.enterprise.exam.shared.dto.remove_these.MovieDTO
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class UserControllerTest : ControllerTestBase() {


    @Test
    fun `can retrieve user`() {

        val user = persistUser(FIRST_USER)
        get(user.email)
                .statusCode(200)
                .body("data.email", equalTo(user.email))
                .body("data.givenName", equalTo(user.givenName))
                .body("data.familyName", equalTo(user.familyName))
    }

    @Test
    fun `getting 404 if not found`() {

        get("does@notexist.com")
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
                .body("message", containsString("givenName size must be between 1 and 250"))
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
                .statusCode(403)
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
    fun `POST to movies returns 400 on constraint violations`() {

        val user = getDummyUser(FIRST_USER)
        user.email = FIRST_USER.email
        user.givenName = "" //NOTE: min name  length is 1

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

    class UserPatchDTO(
            var givenName: String?,
            var familyName: String?
    )

    @Test
    fun `PATCH returns 204 on successful update`() {

        val user = persistUser(FIRST_USER)
        val dto = UserPatchDTO(user.givenName, user.familyName)

        dto.givenName = "updated given name"
        patch(user.email!!, dto, FIRST_USER)
                .statusCode(204)
    }

    @Test  // see note about implementing this in `UserController.updateUser`
    fun `PATCH does try to persist null values`() { //TODO: fix, null restriction not being validated

        val user = persistUser(FIRST_USER)
        val dto = UserPatchDTO(user.givenName, user.familyName)

        dto.familyName = null
        patch(user.email, dto, FIRST_USER)
                .statusCode(400)
    }

    @Test
    fun `PATCH may not modify other users`() {

        val otherUser = persistUser(SECOND_USER)
        val dto = UserPatchDTO(otherUser.givenName, otherUser.familyName)

        patch(otherUser.email, dto, FIRST_USER)
                .statusCode(403)
    }


    @Test
    fun `PATCH returns actually updates in db`() {

        val user = persistUser(FIRST_USER)
        val updatedName = "UPDATED_NAME"
        val dto = UserPatchDTO(updatedName, user.familyName)

        assertNotEquals(user.givenName, updatedName)

        get(user.email)
                .statusCode(200)
                .body("data.givenName", equalTo(user.givenName))

        patch(user.email!!, dto, FIRST_USER)
                .statusCode(204)

        get(user.email)
                .statusCode(200)
                .body("data.givenName", equalTo(updatedName))
    }


    @Test
    fun `can GET timeline of user`() {

        val user = persistUser(FIRST_USER)
        getTimeline(user.email)
                .statusCode(200)
                .body("data.list.size()", equalTo(0)) //as no messages are added
    }


    @Test
    fun `timeline pagination only returns 10 per page`() {

        setupTimelineTest(
                sender = SECOND_USER,
                receiver = FIRST_USER,
                count = 15
        )

        getTimeline(FIRST_USER.email)
                .statusCode(200)
                .body("data.list.size()", equalTo(10))
    }

    @Test
    fun `timeline can follow pagination next-links`() {

        setupTimelineTest(
                sender = SECOND_USER,
                receiver = FIRST_USER,
                count = 25
        )

        val toSecondPage = getTimeline(FIRST_USER.email)
                .statusCode(200)
                .body("data.list.size()", equalTo(10))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        val toThirdPage = getTimeline(FIRST_USER.email, toSecondPage).statusCode(200)
                .body("data.list.size()", equalTo(10))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getTimeline(FIRST_USER.email, toThirdPage)
                .statusCode(200)
                .body("data.list.size()", equalTo(5))
    }

    @Test
    fun `timeline pagination next-link is null on last page`() {

        setupTimelineTest(
                sender = SECOND_USER,
                receiver = FIRST_USER,
                count = 15
        )

        val toSecondPage = getTimeline(FIRST_USER.email)
                .body("data.next", Matchers.notNullValue())
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getTimeline(FIRST_USER.email, toSecondPage)
                .body("data.next", nullValue())
    }


    @Test
    fun `can GET friends of user`() {

        val user = persistUser(FIRST_USER)
        //setupFriendsTest()
        getFriends(user.email)
                .statusCode(200)
                .body("data.list.size()", equalTo(0)) //as no friends are added
    }

    @Test
    fun `all returned friends are actually friends`() {

        val user = persistUser(FIRST_USER)
        persistUsers(20) //people who are not friends with this user
        createFriendsFor(user, 5)

        getFriends(user.email)
                .statusCode(200)
                .body("data.list.size()", equalTo(5))
    }


    @Test
    fun `friends pagination only returns 10 per page`() {

        val user = persistUser(FIRST_USER)
        createFriendsFor(user, 15)

        getFriends( FIRST_USER.email)
                .statusCode(200)
                .body("data.list.size()", equalTo(10))
    }

    @Test
    fun `friends can follow pagination next-links`() {

        val user = persistUser(FIRST_USER)
        createFriendsFor(user, 25)


        val toSecondPage = getFriends(FIRST_USER.email)
                .statusCode(200)
                .body("data.list.size()", equalTo(10))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        val toThirdPage = getFriends(FIRST_USER.email, toSecondPage).statusCode(200)
                .body("data.list.size()", equalTo(10))
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getFriends(FIRST_USER.email, toThirdPage)
                .statusCode(200)
                .body("data.list.size()", equalTo(5))
    }

    @Test
    fun `friends pagination next-link is null on last page`() {

        val user = persistUser(FIRST_USER)
        createFriendsFor(user, 15)

        val toSecondPage = getFriends(FIRST_USER.email)
                .body("data.next", Matchers.notNullValue())
                .extract()
                .jsonPath()
                .get<String>("data.next")

        getFriends(FIRST_USER.email, toSecondPage)
                .body("data.next", nullValue())
    }

    private fun patch(email: String, userDTO: UserPatchDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.email, user.password)
                    .contentType("application/merge-patch+json")
                    .body(userDTO)
                    .patch("/users/${email}")
                    .then()

    private fun post(userDTO: UserDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.email, user.password)
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(userDTO)
                    .post("/users")
                    .then()

    private fun setupTimelineTest(sender: WebSecurityConfigLocalFake.Companion.TestUser, receiver: WebSecurityConfigLocalFake.Companion.TestUser, count: Int) {

        persistUser(sender)
        persistUser(receiver)

        // second user sends a lot of messages receiver first user
        (0 until count).forEach {

            val message = getDummyMessage(sender.email, receiver.email)
            postMessage(message, sender)
                    .statusCode(201)
        }
    }

    private fun createFriendsFor(user: UserEntity, count: Int) {

        (0 until count).forEach {

            val friend = persistUser()

            //making sure that both users sending requests count, as long as accepted
            if (Random.nextInt(0, 2) < 1) {

                persistFriendRequest(friend.email, user.email, FriendRequestStatus.ACCEPTED)
            } else {

                persistFriendRequest(user.email, friend.email, FriendRequestStatus.ACCEPTED)
            }

        }
    }

    private fun getAll(path: String? = null) = given()
            .accept(ContentType.JSON)
            .get(path ?: "/users")
            .then()

    private fun getFriends(email: String, path: String? = null) = given()
            .accept(ContentType.JSON)
            .get(
                    path ?: "/users/$email/friends")
            .then()

    private fun getTimeline(email: String, path: String? = null) = given()
            .accept(ContentType.JSON)
            .get(
                    path ?: "/users/$email/timeline")
            .then()

    private fun get(email: String?) = given()
            .accept(ContentType.JSON)
            .get("/users/$email")
            .then()
}