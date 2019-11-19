package org.enterprise.exam.api.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
class FriendRequestEntity(

        @field:NotNull
        @field:OneToOne
        var sender: UserEntity,

        @field:NotNull
        @field:OneToOne
        var receiver: UserEntity,

        @field:Id
        @field:GeneratedValue
        override var id: Long? = null
): BaseEntity