package org.olaven.enterprise.mock.authentication

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import javax.sql.DataSource

/*
* NOTE: This file is a slightly modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/WebSecurityConfig.kt
* */

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
        private val dataSource: DataSource,
        private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

    @Bean
    override fun userDetailsServiceBean(): UserDetailsService {
        return super.userDetailsServiceBean()
    }

    @Bean
    override fun authenticationManagerBean() : AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {

        http.httpBasic()
                .authenticationEntryPoint { request, response, exception ->
                    // replacing browser popup
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase);
                }
                .and()
                .logout()
                .logoutUrl("/authentication/logout")
                .and()
                //
                .authorizeRequests()
                .antMatchers("/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**").permitAll()
                .antMatchers("/authentication/user").authenticated()
                .antMatchers("/authentication/signUp").permitAll()
                .antMatchers("/authentication/login").permitAll()
                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                //
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    }


    override fun configure(auth: AuthenticationManagerBuilder) {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("""
                     SELECT username, password, enabled
                     FROM users
                     WHERE username=?
                     """)
                .authoritiesByUsernameQuery("""
                     SELECT x.username, y.roles
                     FROM users x, user_entity_roles y
                     WHERE x.username=? and y.user_entity_username=x.username
                     """)
                .passwordEncoder(passwordEncoder)
    }
}