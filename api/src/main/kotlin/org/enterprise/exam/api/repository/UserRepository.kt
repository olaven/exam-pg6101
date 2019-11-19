package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager


@Repository
interface UserRepository : CrudRepository<UserEntity, Long>, PaginatedRepository<UserEntity> {
    fun findByEmail(email: String): Optional<List<UserEntity>>
}// TODO , PaginatedRepository<UserEntity


@Transactional
@Repository
class UserRepositoryImpl(
        private val entityManager: EntityManager
) : PaginatedRepository<UserEntity> {

    override fun getNextPage(size: Int, keysetId: Long?) =
            generalGetNextPage<UserEntity>(keysetId, size,
                    entityManager.createQuery("select user from UserEntity user order by user.id desc, user.email", UserEntity::class.java),
                    entityManager.createQuery("select user from UserEntity user where user.id < :keysetId order by user.id desc, user.email", UserEntity::class.java)
            )
}