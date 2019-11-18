package org.olaven.enterprise.exam.shared.dto

import io.swagger.annotations.ApiModelProperty

class DirectorDTO(

        @ApiModelProperty("Given name of the director")
        var givenName: String,

        @ApiModelProperty("Family name of the director")
        var familyName: String,

        @ApiModelProperty("Movies that this director has directed.")
        var movies: List<MovieDTO>,
        id: String? = null // going to base

) : BaseDTO(id)