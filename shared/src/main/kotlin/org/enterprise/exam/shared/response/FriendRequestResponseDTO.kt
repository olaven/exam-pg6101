package org.enterprise.exam.shared.response

import org.enterprise.exam.shared.dto.FriendRequestDTO


class FriendRequestResponseDTO(
        code: Int? = null,
        data: FriendRequestDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<FriendRequestDTO>(code, data, message, status)