package org.enterprise.exam.authentication

import io.swagger.annotations.ApiModelProperty
import org.enterprise.exam.shared.response.WrappedResponse
import javax.validation.constraints.NotBlank

/*
* NOTE: This file is a modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/AuthDto.kt
* */

class AuthenticationDTO(
        @ApiModelProperty("The ID of the user")
        @get:NotBlank
        var userId: String? = null,

        @ApiModelProperty("The username of the user")
        @get:NotBlank
        var password: String? = null
)

class AuthenticatonResponseDTO(
        code: Int? = null,
        data: AuthenticationDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
) : WrappedResponse<AuthenticationDTO>(code, data, message, status)