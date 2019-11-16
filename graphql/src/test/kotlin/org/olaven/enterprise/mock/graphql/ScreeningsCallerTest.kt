package org.olaven.enterprise.mock.graphql

import com.github.javafaker.Faker
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.google.gson.Gson
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.olaven.enterprise.mock.shared.response.WrappedResponse
import org.olaven.enterprise.mock.shared.dto.Room
import org.olaven.enterprise.mock.shared.dto.ScreeningDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.TimeUnit

/*
* NOTE: This file is loosely based on:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ec66660b6fc426313d1e6a10623f74d55bab3a59/advanced/rest/wiremock/src/test/kotlin/org/tsdes/advanced/rest/wiremock/ConverterRestServiceXmlTest.kt
* */

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ScreeningsCallerTest {

    @Autowired
    private lateinit var screeningsCaller: ScreeningsCaller

    private val faker = Faker()

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

    @Test
    fun `can return specific screening`() {

        val screening = getDummyScreening()
        stubScreeningsCaller(screening)
        val retrieved = screeningsCaller.getScreening(screening.id!!.toLong())

        assertEquals(retrieved!!.id, screening.id)
        assertEquals(retrieved.movieID, screening.movieID)
        assertEquals(retrieved.room, screening.room)
    }

    @Test
    fun `can return available seats in screening`() {

        val screening = getDummyScreening()
        stubScreeningsCaller(screening)
        val seats = screeningsCaller.getAvailableSeats(screening.id!!.toLong())

        assertNotNull(seats)
    }

    private fun stubScreeningsCaller(screening: ScreeningDTO) {

        val response = WrappedResponse(200, screening).validated()
        val json = Gson().toJson(response)

        wiremockServer.stubFor(//prepare a stubbed response for the given request
                WireMock.get(//define the GET request to mock
                        WireMock.urlMatching("/api/screenings/${screening.id}"))
                        // define the mocked response of the GET
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json;")
                                .withBody(json)))
    }

    private fun getDummyScreening() = ScreeningDTO(
            time = faker.date().future(40, TimeUnit.DAYS).toInstant().toEpochMilli(),
            movieID = "SOME MOVIE",
            room = Room.values().random(),
            availableTickets = faker.number().randomDigitNotZero(),
            id = faker.number().randomNumber().toString()
    )
}