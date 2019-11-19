package org.enterprise.exam.api.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.Size

@Entity
class UserEntity (

        @NotNull
        @Size(min = 2, max = 300)
        var email: String,

        @NotNull
        @Size(min = 1, max = 250)
        var givenName: String,

        @NotNull
        @Size(min = 1, max = 350)
        var familyName: String,

        @OneToMany
        var friends: List<UserEntity>,


        @Id
        @GeneratedValue
        override var id: Long? = null

): BaseEntity