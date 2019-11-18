package org.enterprise.exam.shared.dto

import io.swagger.annotations.ApiModelProperty

open class BaseDTO(

        @ApiModelProperty("ID of the object")
        var id: String? = null
)