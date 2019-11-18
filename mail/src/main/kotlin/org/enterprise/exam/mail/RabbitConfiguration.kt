package org.enterprise.exam.mail

import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/*
* NOTE: This file is copied from:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/f141afec6114b5f77fd771df7d3ced809291f961/advanced/amqp/amqp-rest/receiver/src/main/kotlin/org/tsdes/advanced/amqp/rest/receiver/RabbitConfiguration.kt
* */


@Configuration
class RabbitConfiguration {

    @Bean
    fun fanout(): FanoutExchange {
        return FanoutExchange("org.enterprise.exam.mail")
    }

    @Bean
    fun queue(): Queue {
        return AnonymousQueue()
    }

    @Bean
    fun binding(fanout: FanoutExchange,
                queue: Queue): Binding {
        return BindingBuilder.bind(queue).to(fanout)
    }

}