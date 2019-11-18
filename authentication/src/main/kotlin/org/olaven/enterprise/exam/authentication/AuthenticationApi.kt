package org.olaven.enterprise.exam.authentication

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.olaven.enterprise.exam.authentication.user.UserService
import org.olaven.enterprise.exam.shared.response.WrappedResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

/**
 * This file is a modified version of:
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/7984d36992b20955f0210a962ebfbfcbb5139e94/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/RestApi.kt
 */

@RestController
@RequestMapping("/authentication")
class RestApi(
        private val service: UserService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService
) {

    @ApiResponses(
            ApiResponse(code = 200, message = "Successfully retrieved user")
    )
    @RequestMapping("/user")
    fun user(user: Principal): ResponseEntity<WrappedResponse<MutableMap<String, Any>>> {

        val map = mutableMapOf<String, Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)

        return ResponseEntity.status(200).body(
                WrappedResponse(200, map).validated()
        )
    }

    @ApiResponses(
            ApiResponse(code = 204, message = "Successfully created user"),
            ApiResponse(code = 400, message = "There was an error when creating the user")
    )
    @PostMapping(path = ["/signUp"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun signIn(@RequestBody dto: AuthenticationDTO)
            : ResponseEntity<WrappedResponse<AuthenticationDTO>> {

        if (dto.userId == null || dto.password == null) {
            return ResponseEntity.status(400).body(
                    AuthenticatonResponseDTO(400, null, "The user DTO was malformed").validated()
            )
        }

        val userId: String = dto.userId!!
        val password: String = dto.password!!

        val registered = service.createUser(userId, password, setOf("USER"))

        if (!registered) {
            return ResponseEntity.status(400).body(
                    AuthenticatonResponseDTO(400, null, "User could not be created").validated()
            )
        }

        val userDetails = userDetailsService.loadUserByUsername(userId)
        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
        }

        return ResponseEntity.status(204).body(
                AuthenticatonResponseDTO(204, null).validated()
        )
    }

    @ApiOperation("Make a request to login")
    @ApiResponses(
            ApiResponse(code = 204, message = "Successfully logged in"),
            ApiResponse(code = 400, message = "The user could not be found")

    )
    @PostMapping(path = ["/login"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun login(
            @ApiParam("Information about the user")
            @RequestBody dto: AuthenticationDTO
    )
            : ResponseEntity<WrappedResponse<AuthenticationDTO>> {

        val userId: String = dto.userId!!
        val password: String = dto.password!!

        val userDetails = try {
            userDetailsService.loadUserByUsername(userId)
        } catch (e: UsernameNotFoundException) {

            return ResponseEntity.status(400).body(
                    AuthenticatonResponseDTO(400, null, "Could not find username").validated()
            )
        }

        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
            return ResponseEntity.status(204).build()
        }

        return ResponseEntity.status(400).body(
                AuthenticatonResponseDTO(400, null, "There was an error when logging in").validated()
        )
    }

}