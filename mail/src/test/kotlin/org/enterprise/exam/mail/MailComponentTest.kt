package org.enterprise.exam.mail

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.TimeUnit

/*
* NOTE: This file is a modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/b80a95bb0ba74612183abf7d34b9274640419145/advanced/amqp/amqp-rest/receiver/src/test/kotlin/org/tsdes/advanced/amqp/rest/receiver/RestApiTest.kt
* */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(RestApiTest.Companion.Initializer::class)])
@Testcontainers
class RestApiTest {

    companion object {

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

        @Container
        @JvmField
        val rabbitMQ = KGenericContainer("rabbitmq:3").withExposedPorts(5672)

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
                TestPropertyValues
                        .of("spring.rabbitmq.host=" + rabbitMQ.containerIpAddress,
                                "spring.rabbitmq.port=" + rabbitMQ.getMappedPort(5672))
                        .applyTo(configurableApplicationContext.environment)
            }
        }
    }

    @LocalServerPort
    protected var port = 0

    @Autowired
    private lateinit var template: RabbitTemplate

    @Autowired
    private lateinit var fanout: FanoutExchange

    @BeforeEach
    fun clean() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Test
    fun `emails are added to list`() {

        val email = "mail@example.com"
        template.convertAndSend(fanout.name, "", email)

        await().atMost(3, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until {
                    given().port(port)
                            .get("emails")
                            .then()
                            .body("data", hasItem(email))
                            .statusCode(200)
                    true
                }
    }


    @Test
    fun `multiple emails are added to list`() {

        listOf(
                "first@mail.com",
                "second@mail.com",
                "third@mail.com"
        ).onEach {

            template.convertAndSend(fanout.name, "", it)
        }.forEach {

            await().atMost(3, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until {
                        given().port(port)
                                .get("emails")
                                .then()
                                .body("data", hasItem(it))
                                .statusCode(200)
                        true
                    }
        }
    }



    @Test
    fun `endpoint only returns 10 last values`() {

        //NOTE: more than 10
        (0.. 25).map { "$it@mail.com" }.onEach {

            template.convertAndSend(fanout.name, "", it)
        }.forEach {

            await().atMost(5, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until {
                        given().port(port)
                                .get("emails")
                                .then()
                                .body("data.size()", equalTo(10))
                                .statusCode(200)
                        true
                    }
        }
    }

    @Test
    fun `endpoint actually works`() {

        given().port(port)
            .get("emails")
            .then()
            .statusCode(200)
    }
}