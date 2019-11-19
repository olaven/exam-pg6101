package org.enterprise.exam.shared.response

import org.enterprise.exam.shared.dto.MessageDTO

class MessageResponseDTO(
        code: Int? = null,
        data: MessageDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<MessageDTO>(code, data, message, status)