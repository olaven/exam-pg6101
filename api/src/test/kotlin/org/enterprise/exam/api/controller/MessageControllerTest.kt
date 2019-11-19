package org.enterprise.exam.api.controller

import io.restassured.http.ContentType
import org.enterprise.exam.api.WebSecurityConfigLocalFake
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.ADMIN_USER
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.FIRST_USER
import org.enterprise.exam.api.WebSecurityConfigLocalFake.Companion.SECOND_USER
import org.enterprise.exam.shared.dto.MessageDTO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class MessageControllerTest: ControllerTestBase() {

    @BeforeEach
    fun `initialize users before they send messages to each other`() {

        persistUser(FIRST_USER)
        persistUser(SECOND_USER)
    }

    @Test
    fun `successful DELETE returns 204`() {

        val message = persistMessage(FIRST_USER, SECOND_USER)
        delete(message.id!!, ADMIN_USER)
                .statusCode(204)

    }

    @Test
    fun `not allowed to DELETE if not admin`() {

        val message = persistMessage(FIRST_USER, SECOND_USER)
        delete(message.id!!, FIRST_USER)
                .statusCode(403)
        delete(message.id!!, SECOND_USER)
                .statusCode(403)
        delete(message.id!!, ADMIN_USER)
                .statusCode(204)
    }

    @Test
    fun `404 on DELETE if not found`() {

        delete(-1, ADMIN_USER)
                .statusCode(404)
    }

    @Test
    fun `201 on successful POST`() {

        val message = getDummyMessage(FIRST_USER.email, SECOND_USER.email)
        post(message, FIRST_USER)
                .statusCode(201)
    }

    @Test @Disabled
    fun `Can follow locaiton on valid POST`() {}

    @Test @Disabled
    fun `403 if not sender`() {}

    @Test @Disabled
    fun `400 on breaking constraint violations`() {}

    @Test @Disabled
    fun `400 on user not valid user`() {}

    @Test @Disabled
    fun `409 on POST if id is not null`() {}


    private fun post(movie: MessageDTO, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.email, user.password)
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(movie)
                    .post("/messages")
                    .then()


    private fun delete(id: Long, user: WebSecurityConfigLocalFake.Companion.TestUser) =
            authenticated(user.email, user.password)
                    .delete("/messages/${id}")
                    .then()
}