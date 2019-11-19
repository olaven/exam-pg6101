package org.enterprise.exam.shared.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

enum class FriendRequestStatus {
    PENDING,
    REJECTED,
    ACCEPTED
}

@ApiModel("Describes a friend request, with status")
class FriendRequestDTO (

        @ApiModelProperty("Email of the user sending the request")
    val senderEmail: String,

        @ApiModelProperty("Email of the user receiving the request")
    val receiverEmail: String,

        @ApiModelProperty("The current status of the request")
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,

        id: String
): BaseDTO(id)