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

    fun getFriends(email: String, keysetEmail: String?, keysetGivenName: String?, pageSize: Int): List<UserEntity>
    fun getPage(keysetEmail: String?, searchTerm: String?, keysetGivenName: String?, pageSize: Int): List<UserEntity>
}


@Transactional
@Repository
class UserRepositoryImpl(
        private val entityManager: EntityManager
) : CustomUserRepository {

    override fun getFriends(email: String, keysetEmail: String?, keysetGivenName: String?,  pageSize: Int): List<UserEntity> {

        require(!((keysetEmail == null && keysetGivenName != null) || (keysetEmail != null && keysetGivenName == null))) {
            "keysetEmail and keysetGivenName should be both missing, or both present"
        }

        val query =
                entityManager.createQuery(
                        "select user from UserEntity user where " +
                                "((user in (select user from UserEntity user join FriendRequestEntity request on request.receiver = user and user.email <> :email where request.sender.email = :email and request.status = :status)) or" +
                                "(user in (select user from UserEntity user join FriendRequestEntity request on request.sender = user and user.email <> :email where request.receiver.email = :email and request.status = :status)))" +
                                (if (keysetEmail != null) "(user.givenName < :keysetGivenName or (user.givenName= :keysetGivenName and user.email < :email)) " else "") +
                                "order by user.givenName desc, user.email desc" //NOTE: just changed from asc
                        , UserEntity::class.java)
                        .setParameter("status", FriendRequestStatus.ACCEPTED)
                        .setParameter("email", email)


        if (keysetEmail != null) {

            query.setParameter("keysetEmail", keysetEmail)
            query.setParameter("keysetGivenName", keysetGivenName)
        }


        query.maxResults = pageSize
        return query.resultList
    }

    override fun getPage(keysetEmail: String?, keysetGivenName: String?, searchTerm: String?, pageSize: Int): List<UserEntity> {

        require(!((keysetEmail == null && keysetGivenName != null) || (keysetEmail != null && keysetGivenName == null))) {
            "keysetEmail and keysetGivenName should be both missing, or both present"
        }

        if (keysetEmail != null) {

            val query =
                    if (searchTerm != null)
                        entityManager.createQuery("select user from UserEntity user where (user.givenName < :keysetGivenName or (user.givenName= :keysetGivenName and user.email < :email)) and (user.email like :searchTerm) order by user.givenName desc, user.email desc", UserEntity::class.java)
                                .setParameter("searchTerm", "%$searchTerm%")
                    else
                        entityManager.createQuery("select user from UserEntity user where user.givenName < :keysetGivenName or (user.givenName= :keysetGivenName and user.email < :emails) order by user.email desc", UserEntity::class.java)

            query.setParameter("keysetEmail", keysetEmail)
            query.setParameter("keysetGivenName", keysetGivenName)
            query.maxResults = pageSize
            return query.resultList
        } else {


            val query =
                    if (searchTerm != null)
                        entityManager.createQuery("select user from UserEntity user where user.email like :searchTerm order by user.givenName desc, user.email desc", UserEntity::class.java)
                                .setParameter("searchTerm", "%$searchTerm%")
                    else
                        entityManager.createQuery("select user from UserEntity user order by user.givenName desc,  user.email desc", UserEntity::class.java)

            query.maxResults = pageSize
            return query.resultList
        }
    }
}