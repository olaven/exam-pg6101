package org.enterprise.exam.shared.response.remove_these

import org.enterprise.exam.shared.dto.remove_these.ScreeningDTO
import org.enterprise.exam.shared.response.WrappedResponse


class ScreeningResponseDTO(
        code: Int? = null,
        data: ScreeningDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<ScreeningDTO>(code, data, message, status)
