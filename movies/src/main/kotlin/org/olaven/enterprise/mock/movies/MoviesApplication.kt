package org.olaven.enterprise.mock.movies

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.builders.PathSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@SpringBootApplication
//@EnableEurekaClient //TODO
@EnableSwagger2
class MoviesApplication {

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.any())
                .build()
    }
}

// NOTE: will use postgres, as application.yml in _main_ is used
fun main(args: Array<String>) {
    SpringApplication.run(MoviesApplication::class.java, *args)
}