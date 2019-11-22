package org.enterprise.exam.mail

import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/*
* NOTE: This file is inspired by:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/b80a95bb0ba74612183abf7d34b9274640419145/advanced/amqp/amqp-rest/receiver/src/main/kotlin/org/tsdes/advanced/amqp/rest/receiver/RestApi.kt
* */

@RestController
@RequestMapping("/mail")
class WelcomeMessageController {

    // Every email address receiving a welcome message
    private val emails = mutableListOf<String>()

    @RabbitListener(queues = ["#{queue.name}"])
    fun receiveFromAMQP(email: String) {

        print("Sending welcome email to $email!")
        emails.add(email)
    }

    @GetMapping //TODO: maybe paginate?
    fun getMails() = WrappedResponse(200, emails)
}