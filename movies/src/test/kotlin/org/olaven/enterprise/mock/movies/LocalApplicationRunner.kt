package org.olaven.enterprise.mock.movies

import org.springframework.boot.SpringApplication

// NOTE: will use h2, as application.yml in _test_ is used
// requires Postgres on "postgres" domain name
fun main(args: Array<String>) {
    SpringApplication.run(MoviesApplication::class.java, *args)
}