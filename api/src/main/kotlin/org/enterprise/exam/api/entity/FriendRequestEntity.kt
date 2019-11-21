package org.enterprise.exam.api.entity

import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.jetbrains.annotations.NotNull
import java.io.Serializable
import javax.persistence.*

@Entity
class FriendRequestEntity(

        @field:NotNull
        @field:OneToOne
        var sender: UserEntity,

        @field:NotNull
        @field:OneToOne
        var receiver: UserEntity,

        @field:NotNull
        @field:Enumerated(EnumType.STRING)
        var status: FriendRequestStatus = FriendRequestStatus.PENDING,

        @field:Id
        @field:GeneratedValue
        var id: Long? = null
): BaseEntity