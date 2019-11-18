package org.enterprise.exam.api

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy


/*
* NOTE: This file is a slightly modified version of:
*https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/63c0c405c31bcee142d921e9a49fb7a176389eda/advanced/security/distributed-session/ds-greetings/src/test/kotlin/org/tsdes/advanced/security/distributedsession/greetings/WebSecurityConfigLocalFake.kt
* */

/*
  When testing this service in isolation, we would not have the Gateway up and running.
  So we override the default security configuration to use an in-memory
  set of users/passwords.
 */
@Configuration
@EnableWebSecurity
@Order(1)
class WebSecurityConfigLocalFake : WebSecurityConfig() {

    companion object {

        interface TestUser {
            val username: String;
            val password: String
        }

        val FIRST_USER = object : TestUser {
            override val username = "first_user"
            override val password = "first_password"
        }

        val SECOND_USER = object : TestUser {

            override val username = "second_username"
            override val password = "second_password"
        }

        val ADMIN_USER = object : TestUser {

            override val username = "admin"
            override val password = "admin"
        }
    }

    override fun configure(http: HttpSecurity) {
        //call method in parent-class to apply same settings
        super.configure(http)

        http.httpBasic()
                .and()
                //but then override the session management
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {

        auth.inMemoryAuthentication()
                .withUser(FIRST_USER.username).password("{noop}${FIRST_USER.password}").roles("USER").and()
                .withUser(SECOND_USER.username).password("{noop}${SECOND_USER.password}").roles("USER").and()
                .withUser(ADMIN_USER.username).password("{noop}${ADMIN_USER.password}").roles("ADMIN", "USER")
    }
}