package org.olaven.enterprise.exam.shared.response

import org.olaven.enterprise.exam.shared.dto.DirectorDTO


class DirectorResponseDTO(
        code: Int? = null,
        data: DirectorDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<DirectorDTO>(code, data, message, status)