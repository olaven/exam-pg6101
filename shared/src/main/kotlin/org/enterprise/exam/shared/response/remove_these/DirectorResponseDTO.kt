package org.enterprise.exam.shared.response.remove_these

import org.enterprise.exam.shared.dto.remove_these.DirectorDTO
import org.enterprise.exam.shared.response.WrappedResponse


class DirectorResponseDTO(
        code: Int? = null,
        data: DirectorDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<DirectorDTO>(code, data, message, status)