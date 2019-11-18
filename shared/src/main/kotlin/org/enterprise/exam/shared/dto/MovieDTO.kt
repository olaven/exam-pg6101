package org.enterprise.exam.shared.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("Representing a movie")
class MovieDTO(

        @ApiModelProperty("The movie's title")
        var title: String?,

        @ApiModelProperty("Release year of the movie")
        var year: Int?,

        @ApiModelProperty("ID of the movie's director")
        var directorID: String?,

        id: String? = null // going to base

) : BaseDTO(id)