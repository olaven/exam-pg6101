package org.enterprise.exam.shared.response

import org.enterprise.exam.shared.dto.MovieDTO


class MovieResponseDTO(
        code: Int? = null,
        data: MovieDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<MovieDTO>(code, data, message, status)