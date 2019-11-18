package org.olaven.enterprise.exam.shared.response

import org.olaven.enterprise.exam.shared.dto.MovieDTO


class MovieResponseDTO(
        code: Int? = null,
        data: MovieDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<MovieDTO>(code, data, message, status)