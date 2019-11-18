package org.olaven.enterprise.exam.authentication

import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import javax.sql.DataSource


/*
* Before running this:
* start redis, e.g. with docker run -p 6379:6379 redis for now.
* */
fun main(args: Array<String>) {
    SpringApplication.run(AuthenticationApplication::class.java, *args)
}

@Profile("test")
@Configuration
@EnableWebSecurity
@Order(1)
class OverrideSecurityConfig(
        dataSource: DataSource, passwordEncoder: PasswordEncoder
) : WebSecurityConfig(dataSource, passwordEncoder) {

    override fun configure(http: HttpSecurity) {

        super.configure(http)
        http
                .cors()
    }
}

@Profile("test")
@Configuration
class Config {

    //NOTE: Modifying CORS during test, as will run from different origin when developing frontend.
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurerAdapter() {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/authentication/**")
                        .allowedOrigins("http://localhost:8081")
                        .allowCredentials(true)
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS")
            }
        }
    }
}
