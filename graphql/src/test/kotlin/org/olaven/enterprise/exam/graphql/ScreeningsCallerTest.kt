package org.olaven.enterprise.exam.graphql

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/*
* NOTE: This file is loosely based on:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ec66660b6fc426313d1e6a10623f74d55bab3a59/advanced/rest/wiremock/src/test/kotlin/org/tsdes/advanced/rest/wiremock/ConverterRestServiceXmlTest.kt
* */

internal class ScreeningsCallerTest: GraphQLTestBase() {

    @Autowired
    private lateinit var screeningsCaller: ScreeningsCaller

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
}