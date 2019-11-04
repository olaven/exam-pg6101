package org.olaven.enterprise.mock.movies.entity

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
        val id: Long? = null
)