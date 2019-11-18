package org.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.enterprise.exam.graphql.ScreeningsCaller
import org.enterprise.exam.graphql.database.ReservationEntity
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
