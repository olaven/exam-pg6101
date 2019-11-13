package org.olaven.enterprise.mock.authentication

import org.springframework.boot.SpringApplication

// Disable session-store in test-config before running this!
fun main(args: Array<String>) {
    SpringApplication.run(AuthenticationApplication::class.java, *args)
}