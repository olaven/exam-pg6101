package org.olaven.enterprise.mock.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * NOTE: This file is a slightly modified version of:
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/AuthApplication.kt
 */

@EnableSwagger2
@SpringBootApplication(scanBasePackages = ["org.olaven.enterprise.mock"])
class AuthenticationApplication{

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.any())
                .build()
    }

    @Bean
    @Primary //configures jackson to work with Kotlin
    fun objectMapper() = ObjectMapper().apply {
        registerModule(KotlinModule())
    }
}


fun main(args: Array<String>) {
    SpringApplication.run(AuthenticationApplication::class.java, *args)
}