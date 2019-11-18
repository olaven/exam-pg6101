package org.enterprise.exam.api.entity

import javax.persistence.MappedSuperclass

@MappedSuperclass
interface BaseEntity {

    var id: Long?
}
