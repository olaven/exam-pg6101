package org.enterprise.exam.shared.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable

enum class FriendRequestStatus {
    PENDING,
    REJECTED,
    ACCEPTED
}

@ApiModel("Describes a friend request, with status")
class FriendRequestDTO(

        @ApiModelProperty("Email of the user sending the request")
        val senderEmail: String,

        @ApiModelProperty("Email of the user receiving the request")
        var receiverEmail: String,

        @ApiModelProperty("The current status of the request")
        var status: FriendRequestStatus? = null,

        @ApiModelProperty("ID of the message")
        var id: String? = null
) : BaseDTO(), Serializable //Serializable needed to send with AMQP