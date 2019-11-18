package org.enterprise.exam.shared.response

import org.enterprise.exam.shared.dto.ScreeningDTO


class ScreeningResponseDTO(
        code: Int? = null,
        data: ScreeningDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<ScreeningDTO>(code, data, message, status)
