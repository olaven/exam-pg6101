package org.olaven.enterprise.mock.movies.dto

import org.olaven.enterprise.mock.movies.WrappedResponse

/*
    NOTE: This file is a modified version of:
    https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ba8cca8f9e9fd0829ade2bfc1944b58e8235eda6/advanced/rest/wrapper/src/main/kotlin/org/tsdes/advanced/rest/wrapper/ResponseDto.kt
 */

class DirectorDTO (

    val givenName: String,

    val familyName: String,

    val movies: List<MovieDTO>,

    val id: String? = null
)

class DirectorResponseDTO(
        code: Int? = null,
        data: DirectorDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
): WrappedResponse<DirectorDTO>(code, data, message, status)