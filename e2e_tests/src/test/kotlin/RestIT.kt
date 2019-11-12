package org.tsdes.advanced.microservice.gateway.e2etests

import com.github.javafaker.Faker
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
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



    @Test           //TODO: allowing non-admins to showcase integraiton testing
    fun `Can post director and movie, if logged in`() {

        val id = createUniqueId()
        val pwd = "some_password"
        val cookie = registerUser(id, pwd)

        given().cookie("SESSION", cookie)
                .get("/api/authentication/user")
                .then()
                .statusCode(200)
                .body("name", equalTo(id))
                .body("roles", contains("ROLE_USER"))


        val director  = getDirector()

        val directorID = given().cookie("SESSION", cookie) //TODO: user has to have role "ADMIN"
                .contentType(ContentType.JSON)
                .body(director)
                .post("/api/directors")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .get<String>("data.id")



        val movie = getMovie(directorID)

        given().cookie("SESSION", cookie)
                .contentType(ContentType.JSON)
                .body(movie)
                .post("/api/movies")
                .then()
                .statusCode(201)
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

    /*private fun getDirector() = DirectorDTO(
            givenName = faker.name().firstName(),
            familyName = faker.name().lastName(),
            movies = emptyList()
    )*/

    /*private fun getMovie(directorID: String) = MovieDTO(
        title = faker.book().title(),
        year = faker.number().numberBetween(1900, 2000),
        directorID = directorID
)*/


    private fun getDirector() = """
        {
            "givenName": "${faker.name().firstName()}", 
            "familyName": "${faker.name().lastName()}", 
            "movies": []
        }
    """.trimIndent()

    private fun getMovie(directorID: String) = """
        {
            "title": "${faker.book().title()}", 
            "year": "${faker.number().numberBetween(1900, 2000)}", 
            "directorID": "$directorID"
        }
    """.trimIndent()

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