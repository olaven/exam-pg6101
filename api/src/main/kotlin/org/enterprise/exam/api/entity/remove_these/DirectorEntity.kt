package org.enterprise.exam.api.entity.remove_these

import org.enterprise.exam.api.entity.BaseEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.Size

@Entity
class DirectorEntity(

        @field:Size(min = 2, max = 100)
        val givenName: String,

        @field:Size(min = 2, max = 100)
        val familyName: String,

        @OneToMany
        val movies: List<MovieEntity> = emptyList(),

        @Id
        @GeneratedValue
        var id: Long? = null

) : BaseEntity