package org.olaven.enterprise.mock.movies.entity

import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
interface BaseEntity {

    var id: Long?
}
