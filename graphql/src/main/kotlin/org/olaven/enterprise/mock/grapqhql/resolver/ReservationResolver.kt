package org.olaven.enterprise.mock.grapqhql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.olaven.enterprise.mock.grapqhql.types.ReservationInputType
import org.olaven.enterprise.mock.grapqhql.types.ReservationOutputType
import org.springframework.stereotype.Component

@Component
class ReservationResolver : GraphQLResolver<ReservationInputType> {

    fun getId(reservation: ReservationOutputType) =
            reservation.id.toString()
}