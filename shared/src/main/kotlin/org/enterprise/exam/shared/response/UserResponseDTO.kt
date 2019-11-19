package org.enterprise.exam.shared.response

import org.enterprise.exam.shared.dto.UserDTO

class UserResponseDTO(
        code: Int? = null,
        data: UserDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<UserDTO>(code, data, message, status)