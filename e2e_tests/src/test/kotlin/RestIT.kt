package org.tsdes.advanced.microservice.gateway.e2etests

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.TimeUnit

/*
* NOTE: this file is copied from:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ec66660b6fc426313d1e6a10623f74d55bab3a59/advanced/microservice/gateway/gateway-e2e-tests/src/test/kotlin/org/tsdes/advanced/microservice/gateway/e2etests/GatewayRestIT.kt
* */

@Testcontainers
class RestIT : GatewayIntegrationDockerTestBase() {

    companion object {
        init {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }
    }


    @Test @Disabled
    fun `can authenticate and POST movie`() {

        //TODO: add tseting of apis
    }

    @Test @Disabled
    fun `cannot POST movie if not authenticated` () {


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
}