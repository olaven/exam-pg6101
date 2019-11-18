package org.olaven.enterprise.exam.mail

import org.olaven.enterprise.exam.shared.response.WrappedResponse
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/*
* NOTE: This file is inspired by:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/b80a95bb0ba74612183abf7d34b9274640419145/advanced/amqp/amqp-rest/receiver/src/main/kotlin/org/tsdes/advanced/amqp/rest/receiver/RestApi.kt
* */

@RestController
class WelcomeMessageController {

    // Every email address receiving a welcome message
    private val emails = mutableListOf<String>()

    @RabbitListener(queues = ["#{queue.name}"])
    fun receiveFromAMQP(email: String) {

        print("Sending welcome message to $email")
        emails.add(email)
    }

    @GetMapping
    fun getMails() = WrappedResponse(200, emails) //TODO: fix after rename WrappedResponse(200, emails).validated()
}