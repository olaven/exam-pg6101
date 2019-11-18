package org.enterprise.exam.shared.response

import org.enterprise.exam.shared.dto.DirectorDTO


class DirectorResponseDTO(
        code: Int? = null,
        data: DirectorDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<DirectorDTO>(code, data, message, status)