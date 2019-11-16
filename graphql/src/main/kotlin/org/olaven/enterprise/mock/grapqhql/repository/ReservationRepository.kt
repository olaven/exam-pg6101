package org.olaven.enterprise.mock.grapqhql.repository

import org.olaven.enterprise.mock.grapqhql.entity.ReservationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : CrudRepository<ReservationEntity, Long> {
    fun findByUsername(username: String): List<ReservationEntity>
}