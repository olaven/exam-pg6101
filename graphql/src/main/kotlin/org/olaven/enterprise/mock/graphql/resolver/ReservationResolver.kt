package org.olaven.enterprise.mock.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.olaven.enterprise.mock.graphql.ScreeningsCaller
import org.olaven.enterprise.mock.graphql.database.ReservationEntity
import org.springframework.stereotype.Component

@Component
class ReservationResolver(
        private val screeningsCaller: ScreeningsCaller
) : GraphQLResolver<ReservationEntity> {

    fun id(reservation: ReservationEntity) =
            reservation.id.toString()

    fun screening(reservation: ReservationEntity) =
            screeningsCaller.getScreening(reservation.screeningID)

}
