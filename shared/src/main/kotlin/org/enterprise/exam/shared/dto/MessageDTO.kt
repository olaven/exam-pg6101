package org.enterprise.exam.shared.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("Model of a message")
class MessageDTO (

    @ApiModelProperty("The actual content of the message")
    val text: String,

    @ApiModelProperty("The date it was sent")
    val creationDate: Long,

    @ApiModelProperty("The ID of the receiver")
    val receiverID: String,

    @ApiModelProperty("THe ID of the sender/author")
    val senderID: String
)