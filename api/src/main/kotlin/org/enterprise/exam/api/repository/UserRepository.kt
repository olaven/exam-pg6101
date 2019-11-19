package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByEmail(email: String): Optional<List<UserEntity>>
}// TODO , PaginatedRepository<UserEntity