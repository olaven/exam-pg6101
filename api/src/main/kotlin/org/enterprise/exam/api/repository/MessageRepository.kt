package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.MessageEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.persistence.EntityManager

@Repository
interface MessageRepository: CrudRepository<MessageEntity, Long>, CustomMessageRepository //TODO: pagination

interface CustomMessageRepository {

    fun getTimeline(email: String, keysetDate: Long?, pageSize: Int): List<MessageEntity>
}

@Transactional
@Repository
class MessageRepositoryImpl(
        private val entityManager: EntityManager
): CustomMessageRepository {

    override fun getTimeline(email: String, keysetDate: Long?, pageSize: Int): List<MessageEntity> {

        val query = if (keysetDate == null)
            entityManager.createQuery("select message from MessageEntity message where message.receiver.email = :email order by message.creationTime desc, message.id desc", MessageEntity::class.java)
        else {

            val convertedDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(keysetDate), ZoneId.systemDefault())
            entityManager
                    .createQuery("select message from MessageEntity message where message.receiver.email = :email and message.creationTime < :keysetDate order by message.creationTime desc, message.id desc", MessageEntity::class.java)
                    .setParameter("keysetDate", convertedDate)

        }

        query.setParameter("email", email)
        query.maxResults = pageSize
        return query.resultList
    }
}