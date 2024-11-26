package com.fourthwall.cinema.controller

import com.fourthwall.cinema.model.Rating
import com.fourthwall.cinema.model.Review
import com.fourthwall.cinema.service.ReviewService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.websocket.server.PathParam
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/reviews")
class ReviewController(private val reviewService: ReviewService) {

    @Operation(summary = "Returns reviews for given movieId")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "404", description = "Movie with given id not found")
        ]
    )
    @GetMapping
    fun getByMovieId(
        @PathParam("movieId") movieId: Int,
        @RequestParam(defaultValue = "0") pageNo: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ) = reviewService.findMovieReviews(movieId, PageRequest.of(pageNo, pageSize))

    @Operation(summary = "Creates review for given movieId")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Successful operation"),
            ApiResponse(responseCode = "400", description = "Invalid request"),
            ApiResponse(responseCode = "404", description = "Movie with given id not found"),
            ApiResponse(responseCode = "409", description = "Review for given movie from given user already exists")
        ]
    )
    @PostMapping
    fun createReview(@Valid @RequestBody request: CreateReviewRequest): ResponseEntity<CreateReviewResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(reviewService.rateMovie(request))

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
        allowableValues = arrayOf("VERY_BAD", "BAD", "AVERAGE", "GOOD", "VERY_GOOD")
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
        allowableValues = arrayOf("1", "2", "3", "4", "5")
    ) val score: Int
) {
    companion object {
        fun fromReview(review: Review) =
            CreateReviewResponse(review.movie.title, review.userEmail, review.userRate.score)
    }
}