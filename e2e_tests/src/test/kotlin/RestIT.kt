package org.tsdes.advanced.microservice.gateway.e2etests

import com.github.javafaker.Faker
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility
import org.enterprise.exam.shared.dto.FriendRequestDTO
import org.enterprise.exam.shared.dto.MessageDTO
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/*
* NOTE: this file is based on the following:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ec66660b6fc426313d1e6a10623f74d55bab3a59/advanced/microservice/gateway/gateway-e2e-tests/src/test/kotlin/org/tsdes/advanced/microservice/gateway/e2etests/GatewayRestIT.kt
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-e2e-tests/src/test/kotlin/org/tsdes/spring/security/distributedsession/e2etests/DistributedSessionDockerIT.kt
* */

@Testcontainers
class RestIT : GatewayIntegrationDockerTestBase() {

    private var counter = 0
    private val faker = Faker()

    companion object {
        init {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }
    }


    @Test
    fun `can log in`() {

        val id = "some@mail.com"
        val pwd = "bar"

        val cookie = registerUser(id, pwd)

        given().get("/api/authentication/user")
                .then()
                .statusCode(401)

        given().cookie("SESSION", cookie)
                .get("/api/authentication/user")
                .then()
                .statusCode(200)
                .body("data.name", equalTo(id))
                .body("data.roles", contains("ROLE_USER"))


        given().contentType(ContentType.JSON)
                .body("""
                    {"userId": "$id", "password": "$pwd"}
                """.trimIndent())
                .post("/api/authentication/login")
                .then()
                .statusCode(204)
                .cookie("SESSION")
    }

    @Test
    fun `test that GET to users is open`() {

        given().basePath("/api/users")
                .get()
                .then()
                .statusCode(200)
    }

    @Test
    fun `can log in and send friend request`() {

        //NOTE: This test assumes that test users are added.
        val adamSession = login("adam@mail.com", "adampass");

        given().cookie("SESSION", adamSession).contentType(ContentType.JSON)
                .body(FriendRequestDTO(
                        "adam@mail.com", "charlie@mail.com"
                ))
                .post("/api/requests")
                .then()
                .statusCode(201)
    }


    @Test
    fun `can log in and post message`() {

        //NOTE: This test assumes that test users are added.
        val adamSession = login("adam@mail.com", "adampass");

        given().cookie("SESSION", adamSession).contentType(ContentType.JSON)
                .body(MessageDTO(
                        text ="This is my message", creationTime = Instant.now().epochSecond,
                        senderEmail = "adam@mail.com", receiverEmail = "adam@mail.com"
                ))
                .post("/api/messages")
                .then()
                .statusCode(201)
    }

    @Test
    fun testLoadBalance() {


        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until {

                    var passed = false
                    repeat((0..3).count()) {

                        given().get("/api/lb_id")
                                .then()
                                .statusCode(200)
                                .body("data", containsString("A"))

                        given().get("/api/lb_id")
                                .then()
                                .statusCode(200)
                                .body("data", containsString("B"))


                        given().get("/api/lb_id")
                                .then()
                                .statusCode(200)
                                .body("data", containsString("C"))

                        passed = true
                    }

                    passed
                }
    }


    private fun login(username: String, password: String) = given().contentType(ContentType.JSON)
            .body("""
                    {"userId": "$username", "password": "$password"}
                """.trimIndent())
            .post("/api/authentication/login")
            .then()
            .statusCode(204)
            .extract().cookie("SESSION")

    private fun registerUser(id: String, password: String): String {

        val sessionCookie = given().contentType(ContentType.JSON)
                .body("""
                    {"userId": "$id", "password": "$password"}
                """.trimIndent())
                .post("/api/authentication/signUp")
                .then()
                .statusCode(204)
                .header("Set-Cookie", not(equalTo(null)))
                .cookie("SESSION")
                .extract().cookie("SESSION")

        return sessionCookie
    }

    private fun createUniqueId(): String {
        counter++

        val random = Random.nextInt()
        return "foo_$random$counter"
    }
}