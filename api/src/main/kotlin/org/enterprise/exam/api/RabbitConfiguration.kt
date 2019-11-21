package org.enterprise.exam.api

import org.springframework.amqp.core.FanoutExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/*
* NOTE: This file is a slightly modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/69a9378ac0dafa5bb0550fa756a27f30eb0465e0/advanced/amqp/amqp-rest/sender/src/main/kotlin/org/tsdes/advanced/amqp/rest/sender/RabbitConfiguration.kt
* */

/**
 * Besides specifying the @Bean in the
 * @SpringBootApplication annotated class, we can
 * have @Configuration beans.
 */
@Configuration
class RabbitConfiguration {

    @Bean
    fun fanout(): FanoutExchange {
        return FanoutExchange("org.enterprise.exam.friend-request")
    }
}