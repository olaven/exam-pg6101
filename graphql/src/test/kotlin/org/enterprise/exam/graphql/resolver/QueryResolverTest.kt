package org.enterprise.exam.graphql.resolver

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.enterprise.exam.graphql.GraphQLTestBase

internal class QueryResolverTest : GraphQLTestBase() {

    @Test
    fun `can get advertisements`() {

        (0 until 3).forEach {
            persistAdvertisement()
        }


        RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .queryParam("query", "{advertisements{voteCount, message}}")
                .get()
                .then()
                .statusCode(200)
                .body("data.advertisements.size()", equalTo(3))
    }


    @Test
    fun `advertisements always returns 3`() {


        //NOTE: persisting more than 3
        (0 until 10).forEach {
            persistAdvertisement()
        }


        RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .queryParam("query", "{advertisements{voteCount, message}}")
                .get()
                .then()
                .statusCode(200)
                .body("data.advertisements.size()", equalTo(3))
    }
}