package org.enterprise.exam.api.entity.remove_these

import org.enterprise.exam.api.entity.BaseEntity
import org.jetbrains.annotations.NotNull
import org.enterprise.exam.shared.dto.remove_these.Room
import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class ScreeningEntity(

        @field:NotNull
        var time: ZonedDateTime,

        @field:NotNull
        @field:ManyToOne
        var movie: MovieEntity,

        @field:Enumerated(value = EnumType.STRING)
        var room: Room,

        @field:NotNull
        @field:Min(0)
        var availableTickets: Int = 200,

        @field:Id
        @field:GeneratedValue
        var id: Long? = null
) : BaseEntity
