package org.enterprise.exam.api.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
class FriendRequestEntity(

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