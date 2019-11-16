package org.olaven.enterprise.mock.graphql.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Size

@Entity
class ReservationEntity (

        @field:NotNull
        var screeningID: Long,

        @field:NotNull
        @field:Size(min = 2, max = 200)
        var username: String,

        @field:Id
        @field:GeneratedValue
        var id: Long? = null
)