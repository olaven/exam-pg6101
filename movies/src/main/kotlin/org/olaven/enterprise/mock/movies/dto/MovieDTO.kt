package org.olaven.enterprise.mock.movies.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.olaven.enterprise.mock.movies.WrappedResponse

/*
    NOTE: This file is a modified version of:
    https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ba8cca8f9e9fd0829ade2bfc1944b58e8235eda6/advanced/rest/wrapper/src/main/kotlin/org/tsdes/advanced/rest/wrapper/ResponseDto.kt
 */

@ApiModel("Representing a movie")
class MovieDTO (

        @ApiModelProperty("The movie's title")
        var title: String?,

        @ApiModelProperty("Release year of the movie")
        var year: Int?,

        @ApiModelProperty("ID of the movie's director")
        var directorID: String?,

        id: String? = null // going to base

): BaseDTO(id)


class MovieResponseDTO (
        code: Int? = null,
        data: MovieDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
): WrappedResponse<MovieDTO>(code, data, message, status)