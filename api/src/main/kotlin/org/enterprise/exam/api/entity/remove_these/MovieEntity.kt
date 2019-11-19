package org.enterprise.exam.api.entity.remove_these

import org.enterprise.exam.api.entity.BaseEntity
import org.enterprise.exam.api.entity.remove_these.DirectorEntity
import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity
class MovieEntity(

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
        override var id: Long? = null
) : BaseEntity