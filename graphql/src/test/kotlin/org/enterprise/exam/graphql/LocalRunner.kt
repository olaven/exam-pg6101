package org.enterprise.exam.graphql

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

//IMPORTANT: runs on port 8083
fun main(args: Array<String>) {
    SpringApplication.run(GraphQLApplication::class.java, *args)
}

@Profile("test")
@Configuration
class CorsConfig {

    //NOTE: Modifying CORS during test, as will run from different origin when developing frontend.
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurerAdapter() {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .allowedOrigins("localhost")
                        .allowCredentials(true)
                        .allowedMethods("POST")
            }
        }
    }


    //NOTE: mocking away rabbit when running in test
    @Profile("test")
    @Configuration
    internal inner class TestConfiguration {
        @Bean
        fun connectionFactory(): ConnectionFactory {
            return CachingConnectionFactory(MockConnectionFactory())
        }
    }
}