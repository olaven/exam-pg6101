package org.enterprise.exam.api.controller

import io.swagger.annotations.*
import org.enterprise.exam.api.Page
import org.enterprise.exam.api.Transformer
import org.enterprise.exam.api.repository.FriendRequestRepository
import org.enterprise.exam.shared.dto.FriendRequestDTO
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.enterprise.exam.shared.response.FriendRequestResponseDTO
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/requests")
@Api("/requests", description = "Endpoint for friend requests")
class FriendRequestController(
        private val friendRequestRepository: FriendRequestRepository,
        private val transformer: Transformer,
        private val rabbitTemplate: RabbitTemplate,
        private val fanout: FanoutExchange
) {


    @PostMapping
    @ApiOperation("Create a new friend request")
    @ApiResponses(
            ApiResponse(code = 201, message = "The request was created"),
            ApiResponse(code = 400, message = "There was an error in the DTO"),
            ApiResponse(code = 403, message = "User is not allowed to create this request"),
            ApiResponse(code = 409, message = "Client tried to decide ID or status")
    )
    fun createFriendRequest(
            @ApiParam("The request object")
            @RequestBody
            friendRequestDTO: FriendRequestDTO,
            authentication: Authentication
    ): ResponseEntity<WrappedResponse<FriendRequestDTO>> {

        if (authentication.name != friendRequestDTO.senderEmail)
            return ResponseEntity.status(403).body(
                    FriendRequestResponseDTO(403, null, "You are not allowed to create this request").validated()
            )

        if (friendRequestDTO.id != null || friendRequestDTO.status != null) {

            return ResponseEntity.status(409).body(
                    FriendRequestResponseDTO(409, null, "Client wrongly tried to decide ID or status").validated()
            )
        }

        if (friendRequestRepository.existsBetween(authentication.name, friendRequestDTO.receiverEmail)) {

            return ResponseEntity.status(409).body(
                    FriendRequestResponseDTO(409, null, "There already is a friendrequest between these two users")
            )
        }

        return try {

            friendRequestDTO.status = FriendRequestStatus.PENDING
            val persisted = friendRequestRepository.save(
                    transformer.friendRequestToEntity(friendRequestDTO)
            )

            ResponseEntity.created(URI.create("/requests/${persisted.id}")).body(
                    FriendRequestResponseDTO(201, friendRequestDTO.apply { id = persisted.id.toString() }).validated()
            )
        } catch (exception: NoSuchElementException) {

            // other constraint violations are handled by shared exceptionhandler
            ResponseEntity.status(400).body(
                    // NOTE: could arguably have sent 404, as the users were not found.
                    FriendRequestResponseDTO(400, null, "You entered invalid sender/receiver emails").validated()
            )
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Update/answer a friend request")
    @ApiResponses(
            ApiResponse(code = 204, message = "The request was successfully updated"),
            ApiResponse(code = 400, message = "There was an error with the request DTO"),
            ApiResponse(code = 403, message = "You are not allowed to update this resource"),
            ApiResponse(code = 404, message = "The resource was not found")
    )
    fun replaceFriendRequest(
            @ApiParam("The ID of the request")
            @PathVariable("id")
            id: Long,
            @ApiParam("The request object")
            @RequestBody
            friendRequestDTO: FriendRequestDTO,
            authentication: Authentication
    ): ResponseEntity<WrappedResponse<FriendRequestDTO>> {

        if (id != friendRequestDTO.id?.toLong()) return ResponseEntity.status(409).body(
                FriendRequestResponseDTO(409, null, "Wrong ID in body").validated()
        )


        if (!friendRequestRepository.existsById(id)) {

            /*
            * NOTE: a PUT could create a resource if it does not exist.
            * However, that would mean that the client could decide the ID in my case, which is not
            * desirable.
            * */
            return ResponseEntity.status(404).body(
                    FriendRequestResponseDTO(404, null, "The request could not be found")
            )
        }

        val existingEntity = friendRequestRepository.findById(id).get()
        if ((authentication.name != friendRequestDTO.receiverEmail) && (authentication.name != existingEntity.receiver.email))
            return ResponseEntity.status(403).body(
                    FriendRequestResponseDTO(403, null, "You are not allowed to update this request")
            )


        return try {

            val newEntity = transformer.friendRequestToEntity(friendRequestDTO)
            friendRequestRepository.save(newEntity)


            if (friendRequestDTO.status === FriendRequestStatus.ACCEPTED) {

                rabbitTemplate.convertAndSend(fanout.name, "", friendRequestDTO)
            }


            ResponseEntity.status(204).body(
                    FriendRequestResponseDTO(204, null).validated()
            )
        } catch (exception: NoSuchElementException) {

            ResponseEntity.status(400).body(
                    // NOTE: could arguably have sent 404, as the users were not found.
                    FriendRequestResponseDTO(400, null, "You entered invalid sender/receiver emails").validated()
            )
        }
    }

    @GetMapping
    @ApiOperation("Get friend requests")
    @ApiResponses(
            ApiResponse(code = 200, message = "Successfully retrieved friend requests")
    )
    fun getFriendRequests(
            @ApiParam("Specify the email of receiver")
            @RequestParam("receiver", required = true)
            receiver: String,
            @ApiParam("Specify the status of requests")
            @RequestParam("status", required = false)
            status: FriendRequestStatus? = null,
            @ApiParam("The pagination keyset id")
            @RequestParam("keysetId", required = false)
            keysetId: Long?,
            @ApiParam("The pagination keyset email of sender")
            @RequestParam("keysetSenderEmail", required = false)
            keysetSenderEmail: String?
    ): WrappedResponse<Page<FriendRequestDTO>> {

        val pageSize = 10
        val friendRequests = if (status != null) {

            friendRequestRepository.paginatedByReceiverAndStatus(receiver, status, keysetId, keysetSenderEmail, pageSize)
        } else {

            friendRequestRepository.paginatedByReceiver(receiver, keysetId, keysetSenderEmail, pageSize)
        }.map { transformer.friendRequestToDTO(it) }


        val next = if (friendRequests.size == pageSize)
            if (status == null) "/requests?receiver=$receiver&keysetId=${friendRequests.last().id}&keysetSenderEmail=${friendRequests.last().senderEmail}"
            else "/requests?receiver=$receiver&status=$status&keysetId=${friendRequests.last().id}&keysetSenderEmail=${friendRequests.last().senderEmail}"
        else
            null

        val page = Page(friendRequests, next)
        return WrappedResponse(200, page).validated()
    }
}
