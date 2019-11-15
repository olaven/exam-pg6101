package org.olaven.enterprise.mock.grapqhql.types

data class ReservationOutputType(
        val id: String?,
        val screeningID: String,
        val username: String
)


data class ReservationInputType(
        val id: String?,
        val screeningID: String,
        val username: String
)
