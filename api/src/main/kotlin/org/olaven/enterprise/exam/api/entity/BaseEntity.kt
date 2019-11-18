package org.olaven.enterprise.exam.movies.entity

import javax.persistence.MappedSuperclass

@MappedSuperclass
interface BaseEntity {

    var id: Long?
}
