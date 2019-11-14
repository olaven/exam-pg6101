package org.olaven.enterprise.mock.authentication

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * NOTE: This file is a slightly modified version of:
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/AuthApplication.kt
 */


@SpringBootApplication(scanBasePackages = ["org.olaven.enterprise.mock"])
class AuthenticationApplication{

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}


fun main(args: Array<String>) {
    SpringApplication.run(AuthenticationApplication::class.java, *args)
}