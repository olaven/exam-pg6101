package org.olaven.enterprise.mock.movies.responseDTO

import org.olaven.enterprise.mock.shared.WrappedResponse
import org.olaven.enterprise.mock.shared.dto.DirectorDTO


class DirectorResponseDTO(
        code: Int? = null,
        data: DirectorDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<DirectorDTO>(code, data, message, status)