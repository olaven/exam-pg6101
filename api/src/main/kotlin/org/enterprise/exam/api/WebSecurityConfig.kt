package org.enterprise.exam.api

import org.enterprise.exam.shared.dto.UserDTO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

/*
* NOTE: This file is a modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/6d9189f37424630e5344f2552e50e1183fa9203c/advanced/security/distributed-session/ds-user-service/src/main/kotlin/org/tsdes/advanced/security/distributedsession/userservice/WebSecurityConfig.kt
* */

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {

        http
                /*.httpBasic()
                .and()*/
                .authorizeRequests()
                .antMatchers("/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**").permitAll()
                .antMatchers(HttpMethod.GET, "/lb_id").permitAll()
                .antMatchers(HttpMethod.GET, "/users").permitAll()
                .antMatchers(HttpMethod.GET, "/users/{id}").permitAll()
                .antMatchers(HttpMethod.POST, "/users").access("hasRole('USER')") // and @userSecurity.checkId(authentication, #userDTO)
                //TODO: remove the below
                .antMatchers(HttpMethod.GET, "/movies").permitAll()
                .antMatchers(HttpMethod.GET, "/movies/{id}").permitAll()
                .antMatchers(HttpMethod.POST, "/movies").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/movies/{id}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/movies/{id}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/movies/{id}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/directors").permitAll()
                .antMatchers(HttpMethod.GET, "/directors/{id}").permitAll()
                .antMatchers(HttpMethod.POST, "/directors").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/screenings").permitAll()
                .antMatchers(HttpMethod.GET, "/screenings/{id}").permitAll()
                .anyRequest().denyAll() //enabling whitelist
                .and()
                .csrf().disable()
                .sessionManagement()
                //never create a session, but use existing one if provided
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }

    @Bean
    fun userSecurity(): UserSecurity {
        return UserSecurity()
    }
}

/**
 * Custom check. Not only we need a user authenticated, but we also
 * need to make sure that a user can only access his/her data, and not the
 * one of the other users
 */
class UserSecurity {

    fun checkId(authentication: Authentication, id: String): Boolean {

        val username = (authentication.principal as UserDetails).username
        return username == id
    }
}


