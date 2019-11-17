package org.olaven.enterprise.mock.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import com.google.gson.Gson
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import org.olaven.enterprise.mock.graphql.ScreeningsCaller
import org.olaven.enterprise.mock.graphql.database.ReservationEntity
import org.olaven.enterprise.mock.shared.dto.ScreeningDTO
import org.springframework.stereotype.Component

@Component
class ReservationResolver(
        private val screeningsCaller: ScreeningsCaller
) : GraphQLResolver<ReservationEntity> {

    fun id(reservation: ReservationEntity) =
            reservation.id.toString()

    fun screening(reservation: ReservationEntity): DataFetcherResult<ScreeningDTO> {

        val dto = screeningsCaller.getScreening(reservation.screeningID)

        return if (dto == null)
            DataFetcherResult<ScreeningDTO>(null, listOf(GenericGraphQLError("An error occured when fetchign screening ${reservation.screeningID}")))
        else
            DataFetcherResult(dto, emptyList())
    }
}
