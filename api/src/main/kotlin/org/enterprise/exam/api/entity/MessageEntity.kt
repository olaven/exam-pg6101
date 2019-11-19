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

        @NotNull
        @Size(min = 1, max = 1500)
        var text: String,

        @field:NotNull
        var creationTime: ZonedDateTime,

        @NotNull
        @OneToOne
        var sender: UserEntity,

        @NotNull
        @OneToOne
        var receiver: UserEntity,

        @Id
        @GeneratedValue
        override var id: Long? = null
): BaseEntity