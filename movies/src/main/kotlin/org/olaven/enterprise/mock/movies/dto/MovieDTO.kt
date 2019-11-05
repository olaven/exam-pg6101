package org.olaven.enterprise.mock.movies.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.olaven.enterprise.mock.movies.WrappedResponse

@ApiModel("Representing a movie")
class MovieDTO (

        @ApiModelProperty("The movie's title")
        var title: String,

        @ApiModelProperty("Release year of the movie")
        val year: Int,

        @ApiModelProperty("ID of the movie's director")
        val directorID: String,

        @ApiModelProperty("ID of the movie")
        var id: String? = null
): WrappedResponse<MovieDTO>()