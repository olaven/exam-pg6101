package org.enterprise.exam.shared.response.remove_these

import org.enterprise.exam.shared.dto.remove_these.MovieDTO
import org.enterprise.exam.shared.response.WrappedResponse


class MovieResponseDTO(
        code: Int? = null,
        data: MovieDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<MovieDTO>(code, data, message, status)