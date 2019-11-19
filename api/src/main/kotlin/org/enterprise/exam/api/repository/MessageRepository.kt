package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.MessageEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: CrudRepository<MessageEntity, Long> //TODO: pagination