package org.olaven.enterprise.exam.graphql.database

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : CrudRepository<ReservationEntity, Long> {
    fun findByUsername(username: String): List<ReservationEntity>
}