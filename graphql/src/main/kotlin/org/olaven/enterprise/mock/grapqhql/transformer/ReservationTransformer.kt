package org.olaven.enterprise.mock.grapqhql.transformer

import org.olaven.enterprise.mock.grapqhql.entity.ReservationEntity
import org.olaven.enterprise.mock.grapqhql.repository.ReservationRepository
import org.olaven.enterprise.mock.grapqhql.types.ReservationInputType
import org.springframework.stereotype.Component

@Component
class ReservationTransformer(
        reservationRepository: ReservationRepository
) {

    fun toType(entity: ReservationEntity) = ReservationInputType(
            id = entity.id.toString(),
            screeningID = entity.screeningID.toString(),
            username = entity.username
    )

    fun toEntity(inputType: ReservationInputType) = ReservationEntity(
            id = if (inputType.id.isNullOrBlank()) null else inputType.id.toLong(),
            screeningID =  inputType.screeningID.toLong(),
            username = inputType.username
    )
}