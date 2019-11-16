package org.olaven.enterprise.mock.graphql

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

/*
* NOTE: This file is loosely based on:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ec66660b6fc426313d1e6a10623f74d55bab3a59/advanced/rest/wiremock/src/test/kotlin/org/tsdes/advanced/rest/wiremock/ConverterRestServiceXmlTest.kt
* */

internal class ScreeningsCallerTest {

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

    //TODO:
   /* private fun stubScreeningsCaller(id: Long, val screening: ScreeningDTO) {


        val response = WrappedResponse<ScreeningDTO>(200, screening).validated()
        
        wiremockServer.stubFor(//prepare a stubbed response for the given request
                WireMock.get(//define the GET request to mock
                        WireMock.urlMatching("/api/screenings/$id"))
                        // define the mocked response of the GET
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json;")
                                .withBody(id)))
    }*/
}