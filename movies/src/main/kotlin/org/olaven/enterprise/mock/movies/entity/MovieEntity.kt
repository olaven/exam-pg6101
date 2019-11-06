package org.olaven.enterprise.mock.movies.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size
import kotlin.math.min

@Entity
class MovieEntity (

        @field:NotNull
        @field:Size(min = 1, max = 150)
        var title: String,

        @field:NotNull
        @field:Min(1880)
        @field:Max(2500)
        var year: Int,

        @field:ManyToOne
        var director: DirectorEntity,

        @Id
        @GeneratedValue
        val id: Long? = null

        //TODO: rooms (below note)
//title, director and year. Also details about when they are going to be shown in the cinema (time/week and room number).
)