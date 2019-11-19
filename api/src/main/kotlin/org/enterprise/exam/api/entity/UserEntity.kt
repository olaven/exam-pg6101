package org.enterprise.exam.api.entity

import org.jetbrains.annotations.NotNull
import org.springframework.validation.annotation.Validated
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
class UserEntity (

        @field:NotNull
        @field:Size(min = 2, max = 300)
        var email: String,

        @field:NotNull
        @field:Size(min = 1, max = 250)
        var givenName: String?,

        @field:NotNull
        @field:Size(min = 1, max = 350)
        var familyName: String?,

        @field:Id
        @field:GeneratedValue
        override var id: Long? = null

): BaseEntity