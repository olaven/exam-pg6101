package org.olaven.enterprise.mock.movies

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.builders.PathSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
class MoviesApplication {

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

// NOTE: will use postgres, as application.yml in _main_ is used
fun main(args: Array<String>) {
    SpringApplication.run(MoviesApplication::class.java, *args)
}