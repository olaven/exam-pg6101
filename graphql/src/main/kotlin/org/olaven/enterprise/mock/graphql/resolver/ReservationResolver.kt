package org.olaven.enterprise.mock.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.olaven.enterprise.mock.graphql.entity.ReservationEntity
import org.springframework.stereotype.Component

@Component
class ReservationResolver : GraphQLResolver<ReservationEntity> {

    fun id(reservation: ReservationEntity) =
            reservation.id.toString()

    fun screeningId(reservation: ReservationEntity) =
            reservation.screeningID.toString()
}
