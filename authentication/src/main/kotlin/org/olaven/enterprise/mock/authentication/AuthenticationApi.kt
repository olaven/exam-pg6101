package org.olaven.enterprise.mock.authentication

import org.olaven.enterprise.mock.redis.user.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.security.Principal

/*
* NOTE: This file is a slightly modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/RestApi.kt
* */

/**
 * Created by arcuri82 on 08-Nov-17.
 */
@RestController
class AuthenticationApi(
        private val service: UserService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService
) {

    @RequestMapping("/user")
    fun user(user: Principal): ResponseEntity<Map<String, Any>> {
        val map = mutableMapOf<String,Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
        return ResponseEntity.ok(map)
    }

    @PostMapping(path = ["/signUp"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun signIn(@RequestBody dto: AuthenticationDto)
            : ResponseEntity<Void> {

        val userId : String = dto.userId!!
        val password : String = dto.password!!

        val registered = service.createUser(userId, password, setOf("USER"))

        if (!registered) {
            return ResponseEntity.status(400).build()
        }

        val userDetails = userDetailsService.loadUserByUsername(userId)
        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
        }

        return ResponseEntity.status(204).build()
    }

    @PostMapping(path = ["/login"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun login(@RequestBody dto: AuthenticationDto)
            : ResponseEntity<Void> {

        val userId : String = dto.userId!!
        val password : String = dto.password!!

        val userDetails = try{
            userDetailsService.loadUserByUsername(userId)
        } catch (e: UsernameNotFoundException){
            return ResponseEntity.status(400).build()
        }

        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
            return ResponseEntity.status(204).build()
        }

        return ResponseEntity.status(400).build()
    }

}