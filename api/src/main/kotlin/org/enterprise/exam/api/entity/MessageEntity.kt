package org.enterprise.exam.api.entity

import org.jetbrains.annotations.NotNull
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.validation.constraints.Size

@Entity
class MessageEntity (

        @field:NotNull
        @field:Size(min = 1, max = 1500)
        var text: String,

        @field:NotNull
        var creationTime: ZonedDateTime,

        @field:NotNull
        @OneToOne
        var sender: UserEntity,

        @field:NotNull
        @field:OneToOne
        var receiver: UserEntity,

        @field:Id
        @field:GeneratedValue
        override var id: Long? = null
): BaseEntity