package org.olaven.enterprise.mock.graphql

import org.olaven.enterprise.mock.shared.WrappedResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ScreeningsCaller {

    @Value("\${external.api-base}")
    private var apiBase: String? = null

    fun getAvailableSeats(id: Long): String? {

        val client =  RestTemplate()
        val response = client.getForEntity("$apiBase/screenings/$id", WrappedResponse::class.java)


        println(response.body);
        //return getFromJson(json!!, "data.availableSeats") //TODO: is this relevant for circuit breakers?
        return "TODO"
    }
}