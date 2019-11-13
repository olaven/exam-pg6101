package org.olaven.enterprise.mock.movies

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {

        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/movies").permitAll()
                .antMatchers(HttpMethod.GET, "/movies/{id}").permitAll()
                .antMatchers(HttpMethod.POST, "/movies").authenticated() //NOTE: same not as on directors
                .antMatchers(HttpMethod.DELETE, "/movies/{id}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/movies/{id}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/movies/{id}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/directors").permitAll()
                .antMatchers(HttpMethod.GET, "/directors/{id}").permitAll()
                //TODO/NOTE: below should probably only be admin-access, but I wanted to test this in e2e-tests, where I cannot reveal production-admins.
                .antMatchers(HttpMethod.POST, "/directors").authenticated() //.access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/screenings").permitAll()
                .anyRequest().denyAll() //enabling whitelist
                .and()
                .csrf().disable()
                .sessionManagement()
                //never create a session, but use existing one if provided
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }

    @Bean
    fun userSecurity() : UserSecurity {
        return UserSecurity()
    }
}

/**
 * Custom check. Not only we need a user authenticated, but we also
 * need to make sure that a user can only access his/her data, and not the
 * one of the other users
 */
class UserSecurity{

    //TODO: to somwthing like this when booking tickets
    fun checkId(authentication: Authentication, id: String) : Boolean{

        //TODO: make this relevant for movies
        val current = (authentication.principal as UserDetails).username

        return current == id
    }
}

