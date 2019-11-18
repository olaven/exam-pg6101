package org.enterprise.exam.authentication.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/*
    NOTE: This file is copied from:
    https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/db/UserService.kt
 */

/**
 * Created by arcuri82 on 08-Nov-17.
 */
@Service
@Transactional
class UserService(
        private val userCrud: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {


    fun createUser(username: String, password: String, roles: Set<String> = setOf()): Boolean {

        try {
            val hash = passwordEncoder.encode(password)

            if (userCrud.existsById(username)) {
                return false
            }

            val user = UserEntity(username, hash, roles.map { "ROLE_$it" }.toSet())

            userCrud.save(user)

            return true
        } catch (e: Exception) {
            return false
        }
    }

}


