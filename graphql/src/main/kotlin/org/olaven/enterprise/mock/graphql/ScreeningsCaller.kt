package org.olaven.enterprise.mock.graphql

import org.olaven.enterprise.mock.shared.WrappedResponse
import org.olaven.enterprise.mock.shared.dto.ScreeningDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ScreeningsCaller {

    @Value("\${external.api-base}")
    private var apiBase: String? = null

    fun getAvailableSeats(id: Long): Int? {

        val client =  RestTemplate()  //TODO: is this relevant for circuit breakers?
        val response = client.getForEntity("$apiBase/screenings/$id", WrappedResponse::class.java)
        val screening = response.body.data as ScreeningDTO

        return null; // change to screening.availableSeats
    }
}