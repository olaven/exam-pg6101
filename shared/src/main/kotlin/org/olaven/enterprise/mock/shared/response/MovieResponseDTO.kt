package org.olaven.enterprise.mock.shared.response

import org.olaven.enterprise.mock.shared.dto.MovieDTO


class MovieResponseDTO(
        code: Int? = null,
        data: MovieDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<MovieDTO>(code, data, message, status)