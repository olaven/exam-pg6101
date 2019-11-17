package org.olaven.enterprise.mock.graphql

import org.olaven.enterprise.mock.shared.dto.ScreeningDTO
import org.olaven.enterprise.mock.shared.response.ScreeningResponseDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ScreeningsCaller {

    @Value("\${external.api-base}")
    private var apiBase: String? = null

    fun getAvailableSeats(id: Long): Int? {

        val screening = getScreening(id)
        return screening!!.availableTickets
    }

    fun getScreening(id: Long): ScreeningDTO? {

        val client =  RestTemplate()  //TODO: is this relevant for circuit breakers?
        val response = client.getForEntity("$apiBase/screenings/$id", ScreeningResponseDTO::class.java)

        if (response.statusCode == HttpStatus.OK) {
            return response.body.data
        }

        return null
    }
}