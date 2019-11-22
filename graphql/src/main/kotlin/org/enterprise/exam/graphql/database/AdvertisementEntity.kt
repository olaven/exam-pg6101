package org.enterprise.exam.graphql.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class AdvertisementEntity (

        @get:NotBlank
        @get:Size(min = 0, max = 550)
        val message: String,

        @field:NotNull
        @field:Min(0)
        var voteCount: Int,

        @field:Id
        @field:GeneratedValue
        val id: Long? = null
)