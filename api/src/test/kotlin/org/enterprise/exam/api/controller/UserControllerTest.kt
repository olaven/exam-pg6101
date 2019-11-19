package org.enterprise.exam.api.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class UserControllerTest: ControllerTestBase() {


    @Test
    fun `can retrieve user`() {

        val user = persistUser()
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
    

    private fun get(id: Long?) = given()
            .accept(ContentType.JSON)
            .get("/users/$id")
            .then()
}