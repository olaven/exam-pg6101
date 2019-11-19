package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.FriendRequestEntity
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager

@Repository
interface FriendRequestRepository : CrudRepository<FriendRequestEntity, Long>, CustomFriendRequestRepository  //TODO: pagination

interface CustomFriendRequestRepository {

    fun paginatedByReceiver(email: String, keysetId: Long?, pageSize: Int): List<FriendRequestEntity>
    fun paginatedByReceiverAndStatus(email: String, status: FriendRequestStatus, keysetId: Long?, pageSize: Int): List<FriendRequestEntity>
}

class FriendRequestRepositoryImpl(
        private val entityManager: EntityManager
) : CustomFriendRequestRepository {

    override fun paginatedByReceiver(receiver: String, keysetId: Long?, pageSize: Int): List<FriendRequestEntity> {

        //select user from UserEntity user order by user.email desc, user.familyName
        val query =
                if (keysetId != null) entityManager.createQuery(
                        "select request from FriendRequestEntity request where request.receiver.email = :receiver and request.id < :keysetId order by request.sender.email desc, request.id desc"
                        , FriendRequestEntity::class.java).apply { setParameter("keysetId", keysetId) }
                else entityManager.createQuery(
                        "select request from FriendRequestEntity request where request.receiver.email = :receiver order by request.sender.email desc, request.id desc"
                        , FriendRequestEntity::class.java
                )

        query.setParameter("receiver", receiver)
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


