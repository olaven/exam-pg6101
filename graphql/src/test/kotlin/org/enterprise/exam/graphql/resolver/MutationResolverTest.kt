package org.enterprise.exam.graphql.resolver

import io.restassured.RestAssured
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import org.enterprise.exam.graphql.GraphQLTestBase
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class MutationResolverTest : GraphQLTestBase() {

    @Test
    fun `can vote advertisement up`() {

        val advertisement = persistAdvertisement()
        val before = advertisement.voteCount

        val n = Random.nextInt(2, 5)
        (0 until n).forEach {

            voteUp(advertisement.id)
                    .statusCode(200)
                    .body("$", Matchers.hasKey("data"))
                    .body("$", Matchers.not(Matchers.hasKey("errors")))
        }

        val after = advertisementRepository.findById(advertisement.id!!).get().voteCount

        assertEquals(0, before)
        assertEquals(n, after)
    }


    @Test
    fun `can vote advertisement down`() {

        val n = Random.nextInt(2, 5)
        val advertisement = persistAdvertisement()
        advertisement.voteCount = n
        advertisementRepository.save(advertisement); // make sure that the correct is in db

        val before = advertisement.voteCount

        (0 until n).forEach {

            voteDown(advertisement.id)
                    .statusCode(200)
                    .body("$", Matchers.hasKey("data"))
                    .body("$", Matchers.not(Matchers.hasKey("errors")))
        }

        val after = advertisementRepository.findById(advertisement.id!!).get().voteCount

        assertEquals(n, before)
        assertEquals(0, after)
    }


    @Test
    fun `vote is never below 0`() {

        val advertisement = persistAdvertisement()
        assertEquals(0, advertisement.voteCount)

        (0 until 5).forEach {

            voteDown(advertisement.id)
                    .statusCode(200)
                    .body("$", Matchers.hasKey("data"))
                    .body("$", Matchers.not(Matchers.hasKey("errors")))
        }

        val after = advertisementRepository.findById(advertisement.id!!).get().voteCount
        assertEquals(0, after)
    }



    private fun voteUp(advertisementID: Long?) =  RestAssured.given().accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body("""
                    { "query" : "mutation{voteUpAdvertisement(advertisementID:\"$advertisementID\"){voteCount}}" }
                    """.trimIndent())
            .post()
            .then()

    private fun voteDown(advertisementID: Long?) =  RestAssured.given().accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body("""
                    { "query" : "mutation{voteDownAdvertisement(advertisementID:\"$advertisementID\"){voteCount}}" }
                    """.trimIndent())
            .post()
            .then()
}