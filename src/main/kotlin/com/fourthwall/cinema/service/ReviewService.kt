package com.fourthwall.cinema.service

import com.fourthwall.cinema.controller.CreateReviewRequest
import com.fourthwall.cinema.controller.CreateReviewResponse
import com.fourthwall.cinema.controller.GetMovieReviewsResponse
import com.fourthwall.cinema.model.MovieRepository
import com.fourthwall.cinema.model.Review
import com.fourthwall.cinema.model.ReviewRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ReviewService {
    fun findMovieReviews(movieId: Int): GetMovieReviewsResponse
    fun rateMovie(request: CreateReviewRequest): CreateReviewResponse
    fun deleteReview(reviewId: Int)
}

@Service
class ReviewServiceImpl(private val reviewRepository: ReviewRepository, private val movieRepository: MovieRepository) :
    ReviewService {

    @Transactional
    override fun rateMovie(request: CreateReviewRequest): CreateReviewResponse {
        val movie = movieRepository.findById(request.movieId)
            .orElseThrow { MovieNotExistsException(request.movieId) }

        require(request.userEmail.isEmailValid()) { "Invalid email: ${request.userEmail}" }

        reviewRepository.findByMovieIdAndUserEmail(request.movieId, request.userEmail)
            ?.let { throw ReviewAlreadyExistsException(request.movieId, request.userEmail) }

        return CreateReviewResponse.fromReview(
            reviewRepository.save(
                Review(
                    movie = movie,
                    userEmail = request.userEmail,
                    userRate = request.rating,
                )
            )
        )
    }

    override fun deleteReview(reviewId: Int) {
        if (!reviewRepository.existsById(reviewId)) throw ReviewNotExistsException(reviewId)
        reviewRepository.deleteById(reviewId)
    }


    override fun findMovieReviews(movieId: Int): GetMovieReviewsResponse {
        if (!movieRepository.existsById(movieId))
            throw MovieNotExistsException(movieId)
        return GetMovieReviewsResponse.fromReviews(movieId, reviewRepository.findByMovieId(movieId))
    }

    private fun String.isEmailValid(): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
        return emailRegex.toRegex().matches(this)
    }
}

class ReviewAlreadyExistsException(movieId: Int, userEmail: String) :
    RuntimeException("Review for movie with id=$movieId from user=$userEmail already exists")

class ReviewNotExistsException(reviewId: Int) : RuntimeException("Review with id=$reviewId not exists")