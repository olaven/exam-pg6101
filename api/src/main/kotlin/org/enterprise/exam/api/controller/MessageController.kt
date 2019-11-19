package org.enterprise.exam.api.controller

import io.swagger.annotations.*
import org.enterprise.exam.api.Transformer
import org.enterprise.exam.api.entity.MessageEntity
import org.enterprise.exam.api.repository.MessageRepository
import org.enterprise.exam.shared.dto.MessageDTO
import org.enterprise.exam.shared.response.MessageResponseDTO
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.lang.NumberFormatException
import java.net.URI

@RestController
@RequestMapping("/messages")
@Api("/messages", description = "Endpoint for friend messages")
class MessageController(
        private val messageRepository: MessageRepository,
        private val transformer: Transformer
) {

    @GetMapping("/{id}")
    @ApiResponses(
            ApiResponse(code = 200, message = "Successfully retrieved message"),
            ApiResponse(code = 404, message = "Could not find message")
    )
    @ApiOperation("Get single message")
    fun getMessage(
            @ApiParam("The ID of the message")
            @PathVariable("id")
            id: Long
    ): ResponseEntity<WrappedResponse<MessageDTO>> {

        val message = messageRepository.findById(id)
        return if (!message.isPresent) {

            ResponseEntity.status(404).body(
                    MessageResponseDTO(404, null, "Could not find message").validated()
            )
        } else {

            val dto = transformer.messageToDto(message.get())
            ResponseEntity.ok(
                    MessageResponseDTO(200, dto).validated()
            )
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete a message")
    @ApiResponses(
            ApiResponse(code = 204, message = "Successfully deleted message"),
            ApiResponse(code = 403, message = "Only admins may delete messages"),
            ApiResponse(code = 404, message = "Could not find given message")
    )
    fun deleteMessage(
            @ApiParam("The id of the message")
            @PathVariable("id")
            id: Long,
            authentication: Authentication
    ): ResponseEntity<WrappedResponse<MessageDTO>> {

        val entityOptional = messageRepository.findById(id)
        if (!entityOptional.isPresent) {

            return ResponseEntity.status(404).body(
                    MessageResponseDTO(404, null, "Message could not be found.").validated()
            )
        }

        val message = entityOptional.get()
        messageRepository.delete(message)

        return ResponseEntity.status(204).body(
                MessageResponseDTO(204, null).validated()
        )
    }

    @PostMapping
    @ApiOperation("Create a new message")
    @ApiResponses(
            ApiResponse(code = 203, message = "Succesfully created message"),
            ApiResponse(code = 400, message = "There is something wrong with the message"),
            ApiResponse(code = 403, message = "You are not allowed to create this message")
    )
    fun createMessage(
            @ApiParam("The message object")
            @RequestBody
            messageDTO: MessageDTO,
            authentication: Authentication
    ): ResponseEntity<WrappedResponse<MessageDTO>> {

        if (authentication.name != messageDTO.senderEmail) {

            return ResponseEntity.status(403).body(
                    MessageResponseDTO(403, null, "You are not allowed to send this message").validated()
            )
        }

        if (messageDTO.id != null) {

            return ResponseEntity.status(409).body(
                    MessageResponseDTO(409, null, "ID must be null").validated()
            )
        }

        val persisted = messageRepository.save(
                transformer.messageToEntity(messageDTO)
        )

        return ResponseEntity.created(URI.create("/messages/${persisted.id}")).body(
                MessageResponseDTO(201, messageDTO.apply { id = persisted.id.toString() } ).validated()
        )
    }
}