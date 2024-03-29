package org.enterprise.exam.api

import org.enterprise.exam.api.entity.FriendRequestEntity
import org.enterprise.exam.api.entity.MessageEntity
import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.api.repository.UserRepository
import org.enterprise.exam.shared.dto.FriendRequestDTO
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.enterprise.exam.shared.dto.MessageDTO
import org.enterprise.exam.shared.dto.UserDTO
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId

@Component
class Transformer(
        private val userRepository: UserRepository
) {


    fun userToDTO(user: UserEntity) = UserDTO(
            user.email,
            user.givenName,
            user.familyName
    )

    fun userToEntity(user: UserDTO) = UserEntity(
            user.email,
            user.givenName,
            user.familyName
    )

    fun messageToEntity(messageDTO: MessageDTO) = MessageEntity(
            text = messageDTO.text,
            creationTime = Instant.ofEpochMilli(messageDTO.creationTime).atZone(ZoneId.systemDefault()),
            sender = userRepository.findById(messageDTO.senderEmail).get(),
            receiver = userRepository.findById(messageDTO.receiverEmail).get()
            //id = messageDTO.id.toLong()
    )

    fun messageToDto(messageEntity: MessageEntity) = MessageDTO(
            text = messageEntity.text,
            creationTime = messageEntity.creationTime.toInstant().toEpochMilli(),
            receiverEmail = messageEntity.receiver.email,
            senderEmail = messageEntity.sender.email,
            id = messageEntity.id.toString()
    )

    fun friendRequestToDTO(friendRequest: FriendRequestEntity) = FriendRequestDTO(
            senderEmail = friendRequest.sender.email,
            receiverEmail = friendRequest.receiver.email,
            status = friendRequest.status,
            id = friendRequest.id.toString()
    )

    fun friendRequestToEntity(friendRequestDTO: FriendRequestDTO) = FriendRequestEntity(
            sender = userRepository.findById(friendRequestDTO.senderEmail).get(),
            receiver = userRepository.findById(friendRequestDTO.receiverEmail).get(),
            status = friendRequestDTO.status?: FriendRequestStatus.PENDING,
            id = friendRequestDTO.id?.toLong()
    )
}