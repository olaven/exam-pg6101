package org.olaven.enterprise.mock.movies.entity

import org.jetbrains.annotations.NotNull
import java.time.ZonedDateTime
import javax.persistence.*

enum class Room {
    A1, A2, A3,
    B1, B2, B3,
    C1, C2, C3,
    D1, D2, D3,
}


@Entity
class ScreeningEntity(

        @field:NotNull
        var time: ZonedDateTime,

        @field:NotNull
        @field:ManyToOne
        var movie: MovieEntity,

        @field:Enumerated(value = EnumType.STRING)
        var room: Room,

        @field:Id
        @field:GeneratedValue
        override var id: Long? = null
) : BaseEntity
