package com.fourthwall.cinema.service

import com.fourthwall.cinema.controller.GetMovieDetails
import com.fourthwall.cinema.model.Movie
import com.fourthwall.cinema.model.MovieRepository
import com.fourthwall.cinema.omdb.OMDbClient
import org.springframework.stereotype.Service
import java.math.BigDecimal

interface MovieService {
    fun findMovieDetails(movieId: Int): GetMovieDetails
    fun findById(movieId: Int): Movie
    fun findAll(): List<Movie>
}

@Service
class MovieServiceImpl(private val movieRepository: MovieRepository, private val omdbClient: OMDbClient) :
    MovieService {
    override fun findMovieDetails(movieId: Int): GetMovieDetails {
        val movie = movieRepository.findById(movieId).orElseThrow { MovieNotExistsException(movieId) }
        val response = omdbClient.getMovieDetails(movie.imdbId)
        return response?.let {
            with(response) {
                GetMovieDetails(
                    movieId = movieId,
                    movieTitle = title,
                    movieDescription = description,
                    runtime = runtime,
                    releaseDate = releaseDate,
                    imdbId = movie.imdbId,
                    imdbRating = BigDecimal(imdbRating)
                )
            }
        } ?: throw MovieDetailsNotAvailableException(movie.imdbId)
    }

    override fun findById(movieId: Int): Movie =
        movieRepository.findById(movieId).orElseThrow { MovieNotExistsException(movieId) }

    override fun findAll(): List<Movie> = movieRepository.findAll()

}

class MovieNotExistsException(movieId: Int) : RuntimeException("No movie with id=$movieId exists")

class MovieDetailsNotAvailableException(imdbId: String) :
    RuntimeException("Unable to fetch movie details for imdbId=$imdbId")