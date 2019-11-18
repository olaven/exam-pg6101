package org.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.enterprise.exam.graphql.ScreeningsCaller
import org.enterprise.exam.graphql.database.ReservationEntity
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import org.enterprise.exam.shared.dto.ScreeningDTO
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
