package org.olaven.enterprise.mock.graphql

import org.olaven.enterprise.mock.grapqhql.GraphQLApplication
import org.springframework.boot.SpringApplication

fun main(args: Array<String>) {
    SpringApplication.run(GraphQLApplication::class.java, *args)
}