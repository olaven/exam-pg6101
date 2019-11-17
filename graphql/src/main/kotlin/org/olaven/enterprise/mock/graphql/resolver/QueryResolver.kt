package org.olaven.enterprise.mock.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.olaven.enterprise.mock.graphql.database.ReservationEntity
import org.olaven.enterprise.mock.graphql.database.ReservationRepository
import org.springframework.stereotype.Component

@Component
class QueryResolver(
        private val reservationRepository: ReservationRepository
) : GraphQLQueryResolver {

    fun reservationsByUser(username: String): List<ReservationEntity> {

        val results = reservationRepository.findByUsername(username)
        return results
    }

}