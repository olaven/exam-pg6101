package org.enterprise.exam.shared.dto.remove_these

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.enterprise.exam.shared.dto.BaseDTO

@ApiModel("Representing a movie")
class MovieDTO(

        @ApiModelProperty("The movie's title")
        var title: String?,

        @ApiModelProperty("Release year of the movie")
        var year: Int?,

        @ApiModelProperty("ID of the movie's director")
        var directorID: String?,

        @ApiModelProperty("ID of the friend request")
        var id: String? = null

) : BaseDTO()