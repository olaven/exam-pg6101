package org.olaven.enterprise.mock.movies.entity

import org.jetbrains.annotations.NotNull
import org.olaven.enterprise.mock.shared.dto.Room
import java.time.ZonedDateTime
import javax.persistence.*

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
