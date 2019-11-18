package org.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.enterprise.exam.graphql.database.ReservationEntity
import org.enterprise.exam.graphql.database.ReservationRepository
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