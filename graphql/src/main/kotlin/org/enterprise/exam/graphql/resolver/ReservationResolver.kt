package org.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.enterprise.exam.graphql.ScreeningsCaller
import org.enterprise.exam.graphql.database.ReservationEntity
import org.springframework.stereotype.Component

@Component
class ReservationResolver(
        private val screeningsCaller: ScreeningsCaller
) : GraphQLResolver<ReservationEntity> {

    //NOTE: from mock exam, keeping for reference
   /* fun id(reservation: ReservationEntity) =
            reservation.id.toString()

    fun screening(reservation: ReservationEntity): DataFetcherResult<ScreeningDTO> {

        val dto = screeningsCaller.getScreening(reservation.screeningID)

        return if (dto == null)
            DataFetcherResult<ScreeningDTO>(null, listOf(GenericGraphQLError("An error occured when fetchign screening ${reservation.screeningID}")))
        else
            DataFetcherResult(dto, emptyList())
    }*/
}
