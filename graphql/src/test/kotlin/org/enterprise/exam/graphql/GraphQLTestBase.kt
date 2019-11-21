package org.enterprise.exam.graphql

import com.github.javafaker.Faker
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import org.enterprise.exam.graphql.database.ReservationRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GraphQLTestBase {

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @LocalServerPort
    protected var port = 0

    private val faker = Faker()

    @BeforeEach
    fun clean() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/graphql"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        reservationRepository.deleteAll()
    }

    companion object {

        private lateinit var wiremockServer: WireMockServer

        @BeforeAll
        @JvmStatic
        fun initClass() {

            wiremockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8099).notifier(ConsoleNotifier(true)))
            wiremockServer.start()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wiremockServer.stop()
        }
    }

    //NOTE: From mock exam, keeping for reference
   /* protected fun stubScreeningsCaller(screening: ScreeningDTO = getDummyScreening()) {

        val response = WrappedResponse(200, screening).validated()
        val json = Gson().toJson(response)

        GraphQLTestBase.wiremockServer.stubFor(//prepare a stubbed response for the given request
                WireMock.get(//define the GET request to mock
                        WireMock.urlMatching("/api/screenings/${screening.id}"))
                        // define the mocked response of the GET
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json;")
                                .withBody(json)))
    }*/
}