package org.enterprise.exam.shared.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("Representing a user")
class UserDTO(

        @ApiModelProperty("The users email address")
        var email: String,

        @ApiModelProperty("The users given name")
        var givenName: String?,

        @ApiModelProperty("The users family name")
        var familyName: String?

) : BaseDTO()