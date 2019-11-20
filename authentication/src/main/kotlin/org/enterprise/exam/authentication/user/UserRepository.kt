package org.enterprise.exam.authentication.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Repository
interface UserRepository: CrudRepository<UserEntity, String>