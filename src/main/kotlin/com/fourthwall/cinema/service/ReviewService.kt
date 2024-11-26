package com.fourthwall.cinema.service

import com.fourthwall.cinema.controller.CreateReviewRequest
import com.fourthwall.cinema.controller.CreateReviewResponse
import com.fourthwall.cinema.model.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ReviewService {
    fun findMovieReviews(movieId: Int, pageable: Pageable): Page<Review>
    fun rateMovie(request: CreateReviewRequest): CreateReviewResponse
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
            ?.let { throw ReviewAlreadyExists(request.movieId, request.userEmail) }

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

    override fun findMovieReviews(movieId: Int, pageable: Pageable): Page<Review> {
        if (!movieRepository.existsById(movieId))
            throw MovieNotExistsException(movieId)
        return reviewRepository.findByMovieId(movieId, pageable)
    }

    private fun String.isEmailValid(): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
        return emailRegex.toRegex().matches(this)
    }
}

class ReviewAlreadyExists(movieId: Int, userEmail: String) :
    RuntimeException("Review for movie with id=$movieId from user=$userEmail already exists")