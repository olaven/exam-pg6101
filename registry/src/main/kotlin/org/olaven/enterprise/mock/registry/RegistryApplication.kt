package org.olaven.enterprise.mock.registry

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class RegistryApplication

fun main(args: Array<String>) {
    SpringApplication.run(RegistryApplication::class.java, *args)
}