package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager


@Repository
interface UserRepository : CrudRepository<UserEntity, String>, PaginatedRepository<UserEntity> {
    fun findByEmail(email: String): Optional<List<UserEntity>>
}


@Transactional
@Repository
class UserRepositoryImpl(
        private val entityManager: EntityManager
) : PaginatedRepository<UserEntity> {

    override fun getNextPage(size: Int, keysetId: Any?) =
            generalGetNextPage<UserEntity>(keysetId, size,
                    entityManager.createQuery("select user from UserEntity user order by user.email desc, user.familyName", UserEntity::class.java),
                    entityManager.createQuery("select user from UserEntity user where user.email < :keysetId order by user.email desc, user.familyName", UserEntity::class.java)
            )
}