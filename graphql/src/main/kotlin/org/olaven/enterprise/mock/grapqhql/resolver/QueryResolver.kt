package org.olaven.enterprise.mock.grapqhql.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.olaven.enterprise.mock.grapqhql.repository.ReservationRepository
import org.springframework.stereotype.Component

@Component
class QueryResolver(
        private val reservationRepository: ReservationRepository
) : GraphQLQueryResolver {


    fun reservationsByUser(username: String) =
            reservationRepository.findByUsername(username)
}