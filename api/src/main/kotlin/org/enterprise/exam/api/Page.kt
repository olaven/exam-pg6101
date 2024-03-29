package org.enterprise.exam.api

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.jetbrains.annotations.NotNull

@ApiModel("Paginated resources, using keyset pagination")
class Page<T>(

        @ApiModelProperty("The data contained in page")
        @get:NotNull
        var list: List<T> = listOf(),

        @ApiModelProperty("Link to the next page, if it exists")
        var next: String? = null
)
