package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager


@Repository
interface UserRepository : CrudRepository<UserEntity, String>, PaginatedRepository<UserEntity>, CustomUserRepository {

}

interface CustomUserRepository {

    fun getFriends(email: String, keysetId: String?, pageSize: Int): List<UserEntity>
}


@Transactional
@Repository
class UserRepositoryImpl(
        private val entityManager: EntityManager
) : PaginatedRepository<UserEntity>, CustomUserRepository {

    override fun getNextPage(size: Int, keysetId: Any?) =
            generalGetNextPage<UserEntity>(keysetId, size,
                    entityManager.createQuery("select user from UserEntity user order by user.email desc, user.familyName", UserEntity::class.java),
                    entityManager.createQuery("select user from UserEntity user where user.email < :keysetId order by user.email desc, user.familyName", UserEntity::class.java)
            )

    override fun getFriends(email: String, keysetId: String?, pageSize: Int): List<UserEntity> {

        //TODO: pagination, just get this query to work first
        //val query = entityManager.createQuery("select user from UserEntity user, FriendRequestEntity request where (((request.receiver != user and request.receiver.email = :email) or (request.sender != user and request.sender.email = :email)) and request.status = :status)", UserEntity::class.java)

        //TODO: THIS WORKS; BUT NEED OTHER SIDE AS WELL
        val query =
                entityManager.createQuery(
                        "select user from UserEntity user join FriendRequestEntity request on request.receiver = user and user.email <> :email where request.sender.email = :email and request.status = :status"
                        , UserEntity::class.java)
                        .setParameter("status", FriendRequestStatus.ACCEPTED)
                        .setParameter("email", email)

        query.maxResults = pageSize
        return query.resultList
    }
}