package com.fourthwall.cinema.omdb

import com.fasterxml.jackson.annotation.JsonProperty
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

private val logger = KotlinLogging.logger { }

@Service
class OMDbClient {

    @Value("\${integration.omdb.url}")
    lateinit var omdbUrl: String

    @Value("\${integration.omdb.apiKey}")
    lateinit var omdbApiKey: String

    fun getMovieDetails(imdbId: String): OMDbResponse? {
        val restClient = RestClient.create()

        logger.info { "Calling OMDb service with imdbId=$imdbId" }

        return restClient.get()
            .uri(omdbUrl, omdbApiKey, imdbId)
            .accept(APPLICATION_JSON)
            .retrieve()
            .body<OMDbResponse>()
    }
}

data class OMDbResponse(
    @JsonProperty("Title")
    val title: String,
    @JsonProperty("Released")
    val releaseDate: String,
    @JsonProperty("Runtime")
    val runtime: String,
    @JsonProperty("Plot")
    val description: String,
    val imdbRating: String,
)