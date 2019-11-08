package org.tsdes.advanced.microservice.gateway.e2etests

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertTrue
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.not
import org.junit.Ignore
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.TimeUnit

/*
* NOTE: this file is based on the following:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ec66660b6fc426313d1e6a10623f74d55bab3a59/advanced/microservice/gateway/gateway-e2e-tests/src/test/kotlin/org/tsdes/advanced/microservice/gateway/e2etests/GatewayRestIT.kt
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-e2e-tests/src/test/kotlin/org/tsdes/spring/security/distributedsession/e2etests/DistributedSessionDockerIT.kt
* */

@Testcontainers
class RestIT : GatewayIntegrationDockerTestBase() {

    private var counter = 0

    companion object {
        init {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }
    }


    @Test
    fun `can log in`() {

        val id = createUniqueId()
        val pwd = "bar"

        val cookie = registerUser(id, pwd)

        given().get("/api/authentication/user")
                .then()
                .statusCode(401)

        given().cookie("SESSION", cookie)
                .get("/api/authentication/user")
                .then()
                .statusCode(200)
                .body("name", equalTo(id))
                .body("roles", contains("ROLE_USER"))


        given().auth().basic(id, pwd)
                .get("/api/authentication/user")
                .then()
                .statusCode(200)
                .cookie("SESSION")
                .body("name", equalTo(id))
                .body("roles", contains("ROLE_USER"))

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
    fun `test that GET to movies is open`(){

        given().basePath("/api/movies")
                .get()
                .then()
                .statusCode(200)
    }



    @Test @Ignore //TODO: write to movies
    fun testForbiddenToChangeOthers() {

        val firstId = createUniqueId()
        val firstCookie = registerUser(firstId, "123")
        val firstPath = "/user-service/usersInfo/$firstId"

        /*
            In general, it can make sense to have the DTOs in their
            own module, so can be reused in the client directly.
            Otherwise, we would need to craft the JSON manually,
            as done in these tests
         */

        given().cookie("SESSION", firstCookie)
                .get("/api/authentication/user")
                .then()
                .statusCode(200)
                .body("name", equalTo(firstId))
                .body("roles", contains("ROLE_USER"))


        given().cookie("SESSION", firstCookie)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$firstId",
                        "name": "A",
                        "surname": "B",
                        "email": "a@a.com"
                    }
                    """)
                .put(firstPath)
                .then()
                .statusCode(201)


        val secondId = createUniqueId()
        val secondCookie = registerUser(secondId, "123")
        val secondPath = "/user-service/usersInfo/$secondId"

        given().cookie("SESSION", secondCookie)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$secondId",
                        "name": "bla",
                        "surname": "bla",
                        "email": "bla@bla.com"
                    }
                    """)
                .put(secondPath)
                .then()
                .statusCode(201)



        given().cookie("SESSION", firstCookie)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$secondId"
                    }
                    """)
                .put(secondPath)
                .then()
                .statusCode(403)
    }



    @Test
    fun testLoadBalance() {


        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until{

                    var passed = false
                    repeat((0..3).count()) {

                        given().get("/api/movies/lb_id")
                                .then()
                                .statusCode(200)
                                .body("data", containsString("A"))

                        given().get("/api/movies/lb_id")
                                .then()
                                .statusCode(200)
                                .body("data", containsString("B"))


                        given().get("/api/movies/lb_id")
                                .then()
                                .statusCode(200)
                                .body("data", containsString("C"))

                        passed = true
                    }

                    passed
                }
    }

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
        return "foo_$counter"
    }
}