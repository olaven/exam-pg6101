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
@RequestMapping("/emails")
class WelcomeMessageController {

    // Every email address receiving a welcome message
    private val emails = mutableListOf<String>()

    @RabbitListener(queues = ["#{queue.name}"])
    fun receiveFromAMQP(email: String) {

        print("Sending welcome email to $email!")
        emails.add(email)
    }

    /*
     * NOTE: This endpoint does not use pagination.
     *
     * This is mainly because it always returns <= 10 values.
     * It is only returning the most recent emails received
     * by this component.
     *
     * Furthermore, it is not used for anything except testing
     * in `MailComponentTest.kt`. Nor is it exposed through the
     * gateway.
     *
     */
    @GetMapping
    fun getRecentEmails() = WrappedResponse(200, emails.take(10))
}