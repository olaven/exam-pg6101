package org.olaven.enterprise.mock.graphql

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.UriBuilder

/*
* NOTE: This file is loosely based on:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/9416c705eabefa958a276eeb0e68c3ee5ad70bd3/advanced/calling-webservice/from-jvm/src/main/kotlin/org/tsdes/advanced/callingwebservice/fromjvm/OpenWeatherMap.kt
* */

class ScreeningsCaller {

    fun getAvailableSeats(id: Long): String? {

        val json = fetch("/api/movies/screenings/$id")
        return getFromJson(json!!, "data.availableSeats") //TODO: is this relevant for circuit breakers?
    }

    private fun fetch(url: String): String? {

        val uri = UriBuilder
                .fromUri(url)
                .build()

        val client = ClientBuilder.newClient()
        val response = client.target(uri).request("application/json").get()

        return response.readEntity(String::class.java)
    }

    private fun getFromJson(json: String, path: String): String? {

        val parser = JsonParser()
        val parsed = parser.parse(json) as JsonObject

        return parsed.get(path).asString
    }
}