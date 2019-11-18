package org.olaven.enterprise.exam.authentication

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.*
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.olaven.enterprise.exam.authentication.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.GenericContainer

/*
* NOTE: This file is a modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/test/kotlin/org/tsdes/advanced/security/distributedsession/auth/db/SecurityTest.kt
* */

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(SecurityTest.Companion.Initializer::class)])
class SecurityTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @LocalServerPort
    private var port = 0


    companion object {

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

        /*
            Here, going to use an actual Redis instance started in Docker
         */

        @ClassRule
        @JvmField
        val redis = KGenericContainer("redis:latest")
                .withExposedPorts(6379)

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {

                val host = redis.containerIpAddress
                val port = redis.getMappedPort(6379)

                TestPropertyValues
                        .of("spring.redis.host=$host", "spring.redis.port=$port")
                        .applyTo(configurableApplicationContext.environment);
            }
        }
    }

    @Test
    fun `getting appropriate response if user is malformed on signup`() {

        given().contentType(ContentType.JSON)
                .body(AuthenticationDTO(null, "password"))
                .post("/signUp")
                .then()
                .statusCode(400)
                .body("message", containsString("DTO was malformed"))

        given().contentType(ContentType.JSON)
                .body(AuthenticationDTO("username", null))
                .post("/signUp")
                .then()
                .statusCode(400)
                .body("message", containsString("DTO was malformed"))

        given().contentType(ContentType.JSON)
                .body(AuthenticationDTO(null, null))
                .post("/signUp")
                .then()
                .statusCode(400)
                .body("message", containsString("DTO was malformed"))
    }


    @Before
    fun initialize() {
        RestAssured.baseURI = "http://localhost/authentication"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        userRepository.deleteAll()
    }

    @Test
    fun `responses are wrapped`() {

        val userId = "some user id"
        val password = "some password"
        given().contentType(ContentType.JSON)
                .body("""
                    {"userId": "$userId", "password": "$password"}
                """.trimIndent())
                .post("/login")
                .then()
                .body("code", notNullValue())
                .body("message", notNullValue())
                .body("status", notNullValue())
    }

    @Test
    fun `test unauthorized access`() {

        given().get("/user")
                .then()
                .statusCode(401)
    }


    /**
     *   Utility function used to create a new user in the database
     */
    private fun registerUser(id: String, password: String): String {


        val sessionCookie = given().contentType(ContentType.JSON)
                .body(AuthenticationDTO(id, password))
                .post("/signUp")
                .then()
                .statusCode(204)
                .header("Set-Cookie", not(equalTo(null)))
                .extract().cookie("SESSION")

        /*
            From now on, the user is authenticated.
            I do not need to use userid/password in the following requests.
            But each further request will need to have the SESSION cookie.
         */

        return sessionCookie
    }

    private fun checkAuthenticatedCookie(cookie: String, expectedCode: Int) {
        given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(expectedCode)
    }

    @Test
    fun `can log in`() {

        val name = "foo"
        val pwd = "bar"

        checkAuthenticatedCookie("invalid cookie", 401)

        val cookie = registerUser(name, pwd)

        given().get("/user")
                .then()
                .statusCode(401)


        given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(200)
                .body("data.name", equalTo(name))
                .body("data.roles", contains("ROLE_USER"))

        /*
            Same with /login
         */
        val login = given().contentType(ContentType.JSON)
                .body(AuthenticationDTO(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .cookie("SESSION") // new SESSION cookie
                .extract().cookie("SESSION")

        assertNotEquals(login, cookie)
        checkAuthenticatedCookie(login, 200)
    }


    @Test
    fun `400 on wrong login`() {

        val name = "foo"
        val pwd = "bar"

        val noAuth = given().contentType(ContentType.JSON)
                .body(AuthenticationDTO(name, pwd))
                .post("/login")
                .then()
                .statusCode(400)
                .extract().cookie("SESSION")

        //session is not created if not required, eg when 400 user error
        assertNull(noAuth)

        registerUser(name, pwd)

        val auth = given().contentType(ContentType.JSON)
                .body(AuthenticationDTO(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(auth, 200)
    }
}