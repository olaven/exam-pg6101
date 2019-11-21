package org.enterprise.exam.graphql

import org.springframework.stereotype.Service

@Service
class ScreeningsCaller {


    //NOTE: from mock exam, keeping for reference
/*    @Value("\${external.api-base}")
    private var apiBase: String? = null

    fun getAvailableSeats(id: Long): Int? {

        val screening = getScreening(id)
        return screening!!.availableTickets
    }

    fun getScreening(id: Long): ScreeningDTO? {

        val client =  RestTemplate()
        val response = client.getForEntity("$apiBase/screenings/$id", ScreeningResponseDTO::class.java)

        if (response.statusCode == HttpStatus.OK) {
            return response.body.data
        }

        return null
    }*/
}