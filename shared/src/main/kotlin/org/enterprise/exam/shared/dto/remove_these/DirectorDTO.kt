package org.enterprise.exam.shared.dto.remove_these

import io.swagger.annotations.ApiModelProperty
import org.enterprise.exam.shared.dto.BaseDTO

class DirectorDTO(

        @ApiModelProperty("Given name of the director")
        var givenName: String,

        @ApiModelProperty("Family name of the director")
        var familyName: String,

        @ApiModelProperty("Movies that this director has directed.")
        var movies: List<MovieDTO>,

        @ApiModelProperty("ID of the director")
        var id: String? = null
) : BaseDTO()