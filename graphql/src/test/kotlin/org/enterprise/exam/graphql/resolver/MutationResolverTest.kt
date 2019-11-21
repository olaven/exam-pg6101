package org.enterprise.exam.graphql.resolver

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.enterprise.exam.graphql.GraphQLTestBase

internal class MutationResolverTest : GraphQLTestBase() {

    /*@Test
    fun `can create reservation`() {

        val screening = getDummyScreening()
        val username = "some_user"
        val seatCount = 2
        // TODO: This tests returns 400, something is probably wrong with the body I am sending
        stubScreeningsCaller(screening)

        RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("""
                    { "query" : "mutation{create(screeningID:\"${screening.id}\", username:\"$username\", seatCount:$seatCount)}" }
                    """.trimIndent())
                .post()
                .then()
                .statusCode(200)
                .body("$", Matchers.hasKey("data"))
                .body("$", Matchers.not(Matchers.hasKey("errors")))
    }*/
}