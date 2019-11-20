package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager


@Repository
interface UserRepository : CrudRepository<UserEntity, String>, CustomUserRepository

interface CustomUserRepository {

    fun getFriends(email: String, keysetId: String?, pageSize: Int): List<UserEntity>
    fun getPage(keysetEmail: String?, searchTerm: String?, pageSize: Int): List<UserEntity>
}


@Transactional
@Repository
class UserRepositoryImpl(
        private val entityManager: EntityManager
) : CustomUserRepository {

    override fun getFriends(email: String, keysetId: String?, pageSize: Int): List<UserEntity> {

        val query =
                entityManager.createQuery(
                        "select user from UserEntity user where " +
                                "((user in (select user from UserEntity user join FriendRequestEntity request on request.receiver = user and user.email <> :email where request.sender.email = :email and request.status = :status)) or" +
                                "(user in (select user from UserEntity user join FriendRequestEntity request on request.sender = user and user.email <> :email where request.receiver.email = :email and request.status = :status)))" +
                                (if (keysetId != null) " and user.email > :keysetId " else "") +
                                        "order by user.email asc"
                        , UserEntity::class.java)
                        .setParameter("status", FriendRequestStatus.ACCEPTED)
                        .setParameter("email", email)


        if (keysetId != null)
            query.setParameter("keysetId", keysetId)

        query.maxResults = pageSize
        return query.resultList
    }

    override fun getPage(keysetEmail: String?, searchTerm: String?, pageSize: Int): List<UserEntity> {

        if (keysetEmail != null) {

            val query =
                    if (searchTerm != null)
                        entityManager.createQuery("select user from UserEntity user where user.email like :searchTerm: and user.email < :keysetEmail order by user.email desc", UserEntity::class.java)
                                .setParameter("searchTerm", "%$searchTerm%")
                    else
                        entityManager.createQuery("select user from UserEntity user where user.email < :keysetEmail order by user.email desc", UserEntity::class.java)

            query.setParameter("keysetEmail", keysetEmail)
            query.maxResults = pageSize
            return query.resultList
        } else {

            val query =
                    if (searchTerm != null)
                        entityManager.createQuery("select user from UserEntity user where user.email like :searchTerm order by user.email desc", UserEntity::class.java)
                                .setParameter("searchTerm", "%$searchTerm%")
                    else
                        entityManager.createQuery("select user from UserEntity user order by user.email desc", UserEntity::class.java)

            query.maxResults = pageSize
            return query.resultList
        }
    }
}