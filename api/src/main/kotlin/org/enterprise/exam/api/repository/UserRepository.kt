package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : CrudRepository<UserEntity, Long>// TODO , PaginatedRepository<UserEntity