package org.olaven.enterprise.mock.shared.response

import org.olaven.enterprise.mock.shared.dto.ScreeningDTO


class ScreeningResponseDTO(
        code: Int? = null,
        data: ScreeningDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<ScreeningDTO>(code, data, message, status)
