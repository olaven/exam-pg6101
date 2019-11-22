package org.enterprise.exam.graphql

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/*
* NOTE: This is a modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/0bd7a6248ac2a1b497f04cf18d3ce5af02cd8b52/advanced/graphql/news-graphql/src/main/kotlin/org/tsdes/advanced/graphql/newsgraphql/NewsGraphQLApplication.kt
* */

@SpringBootApplication(scanBasePackages = ["org.enterprise.exam"])
@EnableJpaRepositories(basePackages = ["org.enterprise.exam"])
@EntityScan(basePackages = ["org.enterprise.exam"])
class GraphQLApplication


fun main(args: Array<String>) {
    SpringApplication.run(GraphQLApplication::class.java, *args)
}