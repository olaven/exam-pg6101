package org.tsdes.advanced.microservice.gateway.e2etests

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit

/*
* NOTE: This file is a modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/a51045abd1184074d02d825d2d145ba708e54bc5/advanced/microservice/gateway/gateway-e2e-tests/src/test/kotlin/org/tsdes/advanced/microservice/gateway/e2etests/GatewayIntegrationDockerTestBase.kt
* */

@Testcontainers
abstract class GatewayIntegrationDockerTestBase {

    companion object {

        init {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
            RestAssured.port = 80
        }

        class KDockerComposeContainer(id: String, path: File) : DockerComposeContainer<KDockerComposeContainer>(id, path)

        @Container
        @JvmField
        val env = KDockerComposeContainer("gateway", File("../docker-compose.yml"))
                /*
                    In the docker-compose file, I did not expose the port for Eureka.
                    Nor I made a route for it in the gateway.
                    However, I need it in these tests, as need to query it to wait for it
                    be fully initialized.
                    So, I expose it here.
                 */
                .withExposedService("registry", 8761,
                        /*
                            By default, Testcontainer will wait up to 60 seconds for the exposed
                            port to be accessible.
                            This is too low value on free CIs like Travis when there are many
                            Docker images running
                         */
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(480))) //NOTE: was 240, increased due to slow computer
                .withLocalCompose(true)


        @BeforeAll
        @JvmStatic
        fun waitForServer() {

            /*
                Need to wait for a bit, because it can take up to 2 minutes before
                Eureka is fully initialized and all changes are propagated to all
                of its clients. See:
                https://github.com/Netflix/eureka/wiki/Understanding-eureka-client-server-communication

                Note: here I am using the Awaitility library to do such waits
             */

            await().atMost(180, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until {

                        given().get("/index.html").then().statusCode(200)

                        given().accept("application/json;charset=UTF-8")
                                .get("/api/users")
                                .then().statusCode(200)

                        given().baseUri("http://${env.getServiceHost("registry_1", 8761)}")
                                .port(env.getServicePort("registry_1", 8761))
                                .get("/eureka/apps")
                                .then()
                                .body("applications.application.instance.size()", equalTo(4))

                        given().get("http://localhost:80/api/authentication/user").then().statusCode(401)

                        true
                    }
        }

    }

}