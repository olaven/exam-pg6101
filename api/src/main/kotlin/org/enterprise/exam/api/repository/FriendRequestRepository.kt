package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.FriendRequestEntity
import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
interface FriendRequestRepository : CrudRepository<FriendRequestEntity, Long>, CustomFriendRequestRepository  //TODO: FIX pagination

interface CustomFriendRequestRepository {

    fun paginatedByReceiver(email: String, keysetId: Long?, pageSize: Int): List<FriendRequestEntity>
    fun paginatedByReceiverAndStatus(email: String, status: FriendRequestStatus, keysetId: Long?, pageSize: Int): List<FriendRequestEntity>
    fun areFriends(first: String, second: String): Boolean
}

class FriendRequestRepositoryImpl(
        private val entityManager: EntityManager
) : CustomFriendRequestRepository {

    override fun areFriends(first: String, second: String): Boolean {

        val firstQuery = entityManager.createQuery("select request from FriendRequestEntity request where request.sender.email = :first and request.receiver.email = :second and request.status = :status", FriendRequestEntity::class.java)
                .setParameter("first", first)
                .setParameter("second", second)
                .setParameter("status", FriendRequestStatus.ACCEPTED)

        val secondQuery = entityManager.createQuery("select request from FriendRequestEntity request where request.sender.email = :second and request.receiver.email = :first and request.status = :status", FriendRequestEntity::class.java)
                .setParameter("first", first)
                .setParameter("second", second)
                .setParameter("status", FriendRequestStatus.ACCEPTED)

        val firstResult = firstQuery.resultList.size > 0
        val secondResult = secondQuery.resultList.size > 0

        return firstResult || secondResult
    }

    override fun paginatedByReceiver(email: String, keysetId: Long?, pageSize: Int): List<FriendRequestEntity> {

        //select user from UserEntity user order by user.email desc, user.familyName
        val query =
                if (keysetId != null) entityManager.createQuery(
                        "select request from FriendRequestEntity request where request.receiver.email = :email and request.id < :keysetId order by request.id desc"
                        , FriendRequestEntity::class.java)
                        .setParameter("keysetId", keysetId)
                else entityManager.createQuery(
                        "select request from FriendRequestEntity request where request.receiver.email = :email order by request.id desc"
                        , FriendRequestEntity::class.java
                )

        query.setParameter("email", email)
        query.maxResults = pageSize
        return query.resultList
    }

    override fun paginatedByReceiverAndStatus(receiver: String, status: FriendRequestStatus, keysetId: Long?, pageSize: Int): List<FriendRequestEntity> {

        val query =
                if (keysetId != null) entityManager.createQuery(
                        "select request from FriendRequestEntity request where request.status = :status and request.receiver.email = :receiver and request.id < :keysetId order by request.sender.email, request.id desc"
                        , FriendRequestEntity::class.java).apply {

                    setParameter("keysetId", keysetId)
                }
                else entityManager.createQuery(
                        "select request from FriendRequestEntity request where request.status = :status and request.receiver.email = :receiver  order by request.sender.email, request.id desc"
                        , FriendRequestEntity::class.java
                )

        query.setParameter("receiver", receiver)
        query.setParameter("status", status)
        query.maxResults = pageSize
        return query.resultList
    }
}



