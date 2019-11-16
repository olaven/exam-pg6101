package org.olaven.enterprise.mock.graphql.repository

import org.olaven.enterprise.mock.graphql.entity.ReservationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : CrudRepository<ReservationEntity, Long> {
    fun findByUsername(username: String): List<ReservationEntity>
}