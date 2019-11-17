package org.olaven.enterprise.mock.mail

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MoviesApplication

fun main(args: Array<String>) {
    SpringApplication.run(MoviesApplication::class.java, *args)
}