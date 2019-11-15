package org.olaven.enterprise.mock.authentication

import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter


// Disable session-store in test-config before running this!
fun main(args: Array<String>) {
    SpringApplication.run(AuthenticationApplication::class.java, *args)
}

@Profile("test")
@Configuration
class MyConfiguration {

    //NOTE: Modifying CORS during test, as will run from different origin when developing frontend.
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurerAdapter() {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8081")
                        .allowCredentials(true)
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
            }
        } //NOTE: need to have redis running with  docker run -p 6379:6379 redis for now. TODO: perhaps override securityconfig like in movies
    }
}