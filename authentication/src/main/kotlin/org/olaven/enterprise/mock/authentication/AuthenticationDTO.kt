package org.olaven.enterprise.mock.authentication

import org.olaven.enterprise.mock.rest.WrappedResponse
import javax.validation.constraints.NotBlank

/*
* NOTE: This file is copied from:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/AuthDto.kt
* */

class AuthenticationDTO(
        @get:NotBlank
        var userId : String? = null,

        @get:NotBlank
        var password: String? = null
)

class AuthenticatonResponseDTO (
        code: Int? = null,
        data: AuthenticationDTO? = null,
        message: String? = null,
        status: ResponseStatus? = null
): WrappedResponse<AuthenticationDTO>(code, data, message, status)