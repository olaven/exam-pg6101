package org.olaven.enterprise.mock.graphql.resolver

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.olaven.enterprise.mock.graphql.GraphQLTestBase

internal class QueryResolverTest : GraphQLTestBase() {

    @Test
    fun `can get all by a user`() {

        val username = "some user"
        val screening = getDummyScreening()
        stubScreeningsCaller(screening)

        persistReservations(username, screening.id!!, 1)
        persistReservations(username, screening.id!!, 1)
        persistReservations(username, screening.id!!, 1)

        RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .queryParam("query", "{reservationsByUser(username:\"$username\"){id, seatCount}}")
                .get()
                .then()
                .statusCode(200)
                .body("data.reservationsByUser.size()", equalTo(3))

    }
}