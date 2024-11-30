package com.fourthwall.cinema.controller

import com.fourthwall.cinema.model.Rating
import com.fourthwall.cinema.model.Review
import com.fourthwall.cinema.service.ReviewService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.websocket.server.PathParam
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger { }

@RestController
@RequestMapping("/v1/reviews")
@SecurityRequirement(name = "cinema")
class ReviewController(private val reviewService: ReviewService) {

    @Operation(summary = "Returns reviews for given movieId")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "401", description = "User not authenticated"),
            ApiResponse(responseCode = "403", description = "User has no authorization for given operation"),
            ApiResponse(responseCode = "404", description = "Movie with given id not found")
        ]
    )
    @GetMapping
    fun getByMovieId(
        @PathParam("movieId") movieId: Int
    ): GetMovieReviewsResponse {
        logger.info { "Fetching reviews for movie id=$movieId" }
        return reviewService.findMovieReviews(movieId)
    }

    @Operation(summary = "Creates review for given movieId")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Successful operation"),
            ApiResponse(responseCode = "400", description = "Invalid request"),
            ApiResponse(responseCode = "401", description = "User not authenticated"),
            ApiResponse(responseCode = "403", description = "User has no authorization for given operation"),
            ApiResponse(responseCode = "404", description = "Movie with given id not found"),
            ApiResponse(responseCode = "409", description = "Review for given movie from given user already exists")
        ]
    )
    @PostMapping
    fun createReview(@Valid @RequestBody request: CreateReviewRequest): ResponseEntity<CreateReviewResponse> {
        logger.info { "Creating review for movie id=${request.movieId} " }
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.rateMovie(request))
    }
}

@Schema(description = "Request for review creation")
data class CreateReviewRequest(
    @field:NotNull(message = "Movie id is required")
    @field:Schema(description = "Movie Id", example = "1", type = "int") val movieId: Int,
    @field:NotNull(message = "User email is required")
    @field:Email(
        regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
        message = "User email must be a valid email address"
    )
    @field:Schema(
        description = "Review's author email",
        example = "john@doe.com",
        type = "string"
    ) val userEmail: String,
    @field:NotNull(message = "Rating is required")
    @field:Schema(
        description = "Movie rating",
        example = "AVERAGE",
        type = "string",
        allowableValues = ["VERY_BAD", "BAD", "AVERAGE", "GOOD", "VERY_GOOD"]
    ) val rating: Rating
)

@Schema(description = "Response for review creation")
data class CreateReviewResponse(
    @field:Schema(
        description = "Movie title",
        example = "The Fast and the Furious",
        type = "string"
    ) val movieTitle: String,
    @field:Schema(
        description = "Review's author email",
        example = "john@doe.com",
        type = "string"
    ) val userEmail: String,
    @field:Schema(
        description = "Movie rating",
        example = "3",
        type = "int",
        allowableValues = ["1", "2", "3", "4", "5"]
    ) val score: Int
) {
    companion object {
        fun fromReview(review: Review) =
            with(review) { CreateReviewResponse(movie.title, userEmail, userRate.score) }
    }
}

@Schema(description = "Response for getting movie reviews")
data class GetMovieReviewsResponse(
    @field:Schema(
        description = "Movie id",
        example = "1",
        type = "int"
    ) val movieId: Int,
    @field:Schema(
        description = "List of reviews"
    ) val reviews: List<ReviewDTO>
) {
    companion object {
        fun fromReviews(movieId: Int, reviews: List<Review>) =
            GetMovieReviewsResponse(movieId, reviews.map { ReviewDTO.fromReview(it) })
    }
}

@Schema(description = "Represents review")
data class ReviewDTO(
    @field:Schema(
        description = "Movie rating",
        example = "GOOD",
        type = "string"
    ) val rating: String, @field:Schema(
        description = "Author's email",
        example = "john@doe.com",
        type = "string"
    ) val autor: String
) {
    companion object {
        fun fromReview(review: Review) = with(review) { ReviewDTO(userRate.name, userEmail) }
    }
}
