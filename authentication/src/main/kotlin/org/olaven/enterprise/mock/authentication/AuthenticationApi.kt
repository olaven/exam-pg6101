package org.olaven.enterprise.mock.authentication

import org.olaven.enterprise.mock.authentication.user.UserService
import org.olaven.enterprise.mock.rest.WrappedResponse
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
 * Created by arcuri82 on 08-Nov-17.
 */
@RestController
@RequestMapping("/authentication")
class RestApi(
        private val service: UserService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService
) {

    @RequestMapping("/user")
    fun user(user: Principal): ResponseEntity<WrappedResponse<MutableMap<String, Any>>> {
        val map = mutableMapOf<String, Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)

        return ResponseEntity.status(200).body(
                WrappedResponse(200, map).validated()
        )
    }

    @PostMapping(path = ["/signUp"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun signIn(@RequestBody dto: AuthenticationDTO)
            : ResponseEntity<WrappedResponse<AuthenticationDTO>> {

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

    @PostMapping(path = ["/login"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun login(@RequestBody dto: AuthenticationDTO)
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