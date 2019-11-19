package org.enterprise.exam.api.controller

import io.swagger.annotations.*
import org.enterprise.exam.api.repository.MessageRepository
import org.enterprise.exam.shared.dto.MessageDTO
import org.enterprise.exam.shared.response.MessageResponseDTO
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/messages")
@Api("/messages", description = "Endpoint for friend messages")
class MessageController(
        private val messageRepository: MessageRepository
) {

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
}