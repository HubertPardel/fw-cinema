package com.fourthwall.cinema.controller

import com.fourthwall.cinema.model.Movie
import com.fourthwall.cinema.service.MovieService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

private val logger = KotlinLogging.logger { }

@RestController
@RequestMapping("/v1/movies")
@SecurityRequirement(name = "cinema")
class MovieController(private val movieService: MovieService) {

    @Operation(summary = "Get movie by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "401", description = "User not authenticated"),
            ApiResponse(responseCode = "403", description = "User has no authorization for given operation"),
            ApiResponse(responseCode = "404", description = "Movie with given id not found")
        ]
    )
    @GetMapping("/{movieId}")
    fun getMovie(@PathVariable movieId: Int): ResponseEntity<GetMovieResponse> {
        logger.info { "Fetching movie id=$movieId" }
        return ResponseEntity.status(HttpStatus.OK)
            .body(GetMovieResponse.fromMovie(movieService.findById(movieId)))
    }

    @Operation(summary = "Get all movies")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "401", description = "User not authenticated"),
            ApiResponse(responseCode = "403", description = "User has no authorization for given operation")
        ]
    )
    @GetMapping
    fun getAllMovies(): ResponseEntity<List<GetMovieResponse>> {
        logger.info { "Fetching all movies" }
        return ResponseEntity.status(HttpStatus.OK)
            .body(movieService.findAll().map { GetMovieResponse.fromMovie(it) })
    }

    @Operation(summary = "Get movie details by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "400", description = "Error when fetching move details"),
            ApiResponse(responseCode = "401", description = "User not authenticated"),
            ApiResponse(responseCode = "403", description = "User has no authorization for given operation"),
            ApiResponse(responseCode = "404", description = "Movie with given id not found")
        ]
    )
    @GetMapping("/details/{movieId}")
    fun getMovieDetails(@PathVariable movieId: Int): ResponseEntity<GetMovieDetails> {
        logger.info { "Fetching movie details for movie id=$movieId" }
        return ResponseEntity.status(HttpStatus.OK).body(movieService.findMovieDetails(movieId))
    }
}

@Schema(description = "Response for getting movie")
data class GetMovieResponse(
    @field:Schema(
        description = "Movie id",
        example = "1",
        type = "int"
    ) val movieId: Int,
    @field:Schema(
        description = "Movie title",
        example = "The Fast and the Furious",
        type = "string"
    ) val movieTitle: String,
    @field:Schema(
        description = "IMDb Id",
        example = "tt123456",
        type = "string"
    ) val imdbId: String,
) {
    companion object {
        fun fromMovie(movie: Movie) = with(movie) { GetMovieResponse(id!!, title, imdbId) }
    }
}

@Schema(description = "Response for getting movie details")
data class GetMovieDetails(
    @field:Schema(
        description = "Movie id",
        example = "1",
        type = "int"
    ) val movieId: Int,
    @field:Schema(
        description = "Movie title",
        example = "The Fast and the Furious",
        type = "string"
    ) val movieTitle: String,
    @field:Schema(
        description = "Movie description",
        example = "Movie about cars and stuff",
        type = "string"
    ) val movieDescription: String,
    @field:Schema(
        description = "Runtime",
        example = "110 min",
        type = "string"
    ) val runtime: String,
    @field:Schema(
        description = "Release date",
        example = "22 Jun 2001",
        type = "string",
        format = "date"
    ) val releaseDate: String,
    @field:Schema(
        description = "IMDb Id",
        example = "tt123456",
        type = "string"
    ) val imdbId: String,
    @field:Schema(
        description = "IMDb rating",
        example = "7.5",
        type = "number",
        format = "double"
    ) val imdbRating: BigDecimal
)