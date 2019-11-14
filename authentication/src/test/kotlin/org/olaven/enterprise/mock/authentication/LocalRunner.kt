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

    //NOTE: Disabling CORS during test, as will run from different origin when developing frontend.
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurerAdapter() {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
            }
        }
    }
}