package org.enterprise.exam.api.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
class UserEntity (

        @field:Id
        @field:Email
        @field:NotNull
        @field:Size(min = 2, max = 300)
        var email: String,

        @field:NotBlank
        @field:Size(min = 1, max = 250)
        var givenName: String?,

        @field:NotBlank
        @field:Size(min = 1, max = 350)
        var familyName: String?

): BaseEntity