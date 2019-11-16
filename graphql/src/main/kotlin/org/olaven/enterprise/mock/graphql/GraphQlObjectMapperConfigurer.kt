package org.olaven.enterprise.mock.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import graphql.servlet.ObjectMapperConfigurer
import org.springframework.stereotype.Component

/**
 * NOTE: This file is copied from:
 *https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/0bd7a6248ac2a1b497f04cf18d3ce5af02cd8b52/advanced/graphql/news-graphql/src/main/kotlin/org/tsdes/advanced/graphql/newsgraphql/GraphQlObjectMapperConfigurer.kt
 */

/**
 * GraphQL does not use Jackson configuration in Spring
 *
 * https://github.com/graphql-java/graphql-spring-boot/issues/65
 *
 * this means we need to explicitly activate the support for
 * time date serialization.
 */
@Component
class GraphQlObjectMapperConfigurer : ObjectMapperConfigurer {

    override fun configure(mapper: ObjectMapper) {
        mapper.registerModule(JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
}