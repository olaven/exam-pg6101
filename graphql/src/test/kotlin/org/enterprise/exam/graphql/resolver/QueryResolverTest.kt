package org.enterprise.exam.graphql.resolver

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.enterprise.exam.graphql.GraphQLTestBase

internal class QueryResolverTest : GraphQLTestBase() {

    @Test
    fun `can get advertisements`() {

        val userEmail = "charlie@mail.com"

        (0 until 3).forEach {
            persistAdvertisement()
        }


        RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .queryParam("query", "{advertisementsForUser(userEmail:\"$userEmail\"){voteCount, message}}")
                .get()
                .then()
                .statusCode(200)
                .body("data.advertisementsForUser.size()", equalTo(3))
    }


    @Test
    fun `advertisements always returns 3`() {

        val userEmail = "charlie@mail.com"

        //NOTE: persisting more than 3
        (0 until 10).forEach {
            persistAdvertisement()
        }


        RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .queryParam("query", "{advertisementsForUser(userEmail:\"$userEmail\"){voteCount, message}}")
                .get()
                .then()
                .statusCode(200)
                .body("data.advertisementsForUser.size()", equalTo(3))
    }
}