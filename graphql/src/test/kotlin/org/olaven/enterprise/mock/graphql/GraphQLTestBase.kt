package org.olaven.enterprise.mock.graphql

import com.github.javafaker.Faker
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.google.gson.Gson
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.olaven.enterprise.mock.graphql.database.ReservationEntity
import org.olaven.enterprise.mock.graphql.database.ReservationRepository
import org.olaven.enterprise.mock.shared.dto.Room
import org.olaven.enterprise.mock.shared.dto.ScreeningDTO
import org.olaven.enterprise.mock.shared.response.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.TimeUnit

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

    protected fun persistReservations(username: String, screeningID: String, seatCount: Int = faker.number().numberBetween(1, 10)): ReservationEntity {

        return reservationRepository.save(ReservationEntity(
                screeningID = screeningID.toLong(),
                username = username,
                seatCount = seatCount
        ))
    }

    protected fun getDummyScreening() = ScreeningDTO(
            time = faker.date().future(40, TimeUnit.DAYS).toInstant().toEpochMilli(),
            movieID = "SOME MOVIE",
            room = Room.values().random(),
            availableTickets = faker.number().randomDigitNotZero(),
            id = faker.number().randomNumber().toString()
    )

    protected fun stubScreeningsCaller(screening: ScreeningDTO = getDummyScreening()) {

        val response = WrappedResponse(200, screening).validated()
        val json = Gson().toJson(response)

        GraphQLTestBase.wiremockServer.stubFor(//prepare a stubbed response for the given request
                WireMock.get(//define the GET request to mock
                        WireMock.urlMatching("/api/screenings/${screening.id}"))
                        // define the mocked response of the GET
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json;")
                                .withBody(json)))
    }
}