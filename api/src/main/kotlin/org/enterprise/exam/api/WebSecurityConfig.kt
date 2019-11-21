package org.enterprise.exam.api

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
//@EnableGlobalMethodSecurity(prePostEnabled = true)
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
                .antMatchers(HttpMethod.GET, "/users/{id}/timeline").access("hasRole('USER')")
                .antMatchers(HttpMethod.GET, "/users/{id}/friends").permitAll()
                .antMatchers(HttpMethod.GET, "/users/{id}/friends/{friend}").permitAll()
                .antMatchers(HttpMethod.POST, "/users").access("hasRole('USER')") // and @userSecurity.checkId(authentication, #userDTO)
                .antMatchers(HttpMethod.PATCH, "/users/{id}").access("hasRole('USER')")
                .antMatchers(HttpMethod.DELETE, "/messages/{id}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/messages").access("hasRole('USER')")
                .antMatchers(HttpMethod.GET, "/messages").permitAll()
                .antMatchers(HttpMethod.GET, "/messages/{id}").permitAll()
                .antMatchers(HttpMethod.GET, "/requests").permitAll()
                .antMatchers(HttpMethod.POST, "/requests").access("hasRole('USER')")
                .antMatchers(HttpMethod.PUT, "/requests/{id}").access("hasRole('USER')")
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


