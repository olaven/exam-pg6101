package org.enterprise.exam.shared.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("Model of a message")
class MessageDTO(

        @ApiModelProperty("The actual content of the message")
        var text: String,

        @ApiModelProperty("The date it was sent")
        val creationDate: Long,

        @ApiModelProperty("The email of the receiver")
        val receiverEmail: String,

        @ApiModelProperty("THe email of the sender/author")
        val senderEmail: String,


        @ApiModelProperty("ID of the message")
        var id: String?
) : BaseDTO()