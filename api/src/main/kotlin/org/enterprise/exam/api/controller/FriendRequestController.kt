package org.enterprise.exam.api.controller

import io.swagger.annotations.*
import org.enterprise.exam.api.Transformer
import org.enterprise.exam.api.repository.FriendRequestRepository
import org.enterprise.exam.api.repository.Page
import org.enterprise.exam.shared.dto.FriendRequestDTO
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/requests")
@Api("/requests", description = "Endpoint for friend requests")
class FriendRequestController(
        private val friendRequestRepository: FriendRequestRepository,
        private val transformer: Transformer
) {

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
            keysetId: Long? //TODO: something is going wrong here
    ): WrappedResponse<Page<FriendRequestDTO>> {


        val pageSize = 10
        val friendRequests = if (status != null) {

            friendRequestRepository.paginatedByReceiverAndStatus(receiver, status, keysetId, pageSize)
        } else {

            friendRequestRepository.paginatedByReceiver(receiver, keysetId, pageSize)
        }.map { transformer.friendRequestToDTO(it) }


        val next = if (friendRequests.size == pageSize)
            if (status == null) "/requests?receiver=$receiver&keysetId=${friendRequests.last().id}"
            else "/requests?receiver=$receiver&status=$status&keysetId=${friendRequests.last().id}"
        else
            null

        val page = Page(friendRequests, next)
        return WrappedResponse(200, page).validated()
    }
}
