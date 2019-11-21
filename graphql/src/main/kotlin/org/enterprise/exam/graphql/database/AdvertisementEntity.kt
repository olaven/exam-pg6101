package org.enterprise.exam.graphql.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class AdvertisementEntity (

        @field:Id
        @field:GeneratedValue
        val id: Long,

        @get:NotBlank
        val message: String,

        @field:NotNull
        @field:Min(0)
        var voteCount: Int
)