package com.fourthwall.cinema.service

import com.fourthwall.cinema.model.Movie
import com.fourthwall.cinema.model.MovieRepository
import com.fourthwall.cinema.omdb.OMDbClient
import com.fourthwall.cinema.omdb.OMDbResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class MovieServiceImplTest {
    private val movieRepository: MovieRepository = mockk()
    private val omdbClient: OMDbClient = mockk()

    private val service = MovieServiceImpl(movieRepository, omdbClient)
    private val movieId = 1
    private val title = "title"
    private val imdbId = "tt12345"

    private val movieId2 = 2
    private val title2 = "title2"
    private val imdbId2 = "tt12356"

    @Test
    fun shouldThrowException_whenGettingMovieDetails_ThatNotExist() {
        //given
        every { movieRepository.findById(movieId) } returns Optional.empty()

        //then
        assertFailsWith<MovieNotExistsException> {
            service.findMovieDetails(movieId)
        }
    }

    @Test
    fun shouldThrowException_whenGettingMovieDetails_OmdbCallFails() {
        //given
        every { movieRepository.findById(movieId) } returns Optional.of(Movie(movieId, title, imdbId))
        every { omdbClient.getMovieDetails(imdbId) } returns null

        //then
        assertFailsWith<MovieDetailsNotAvailableException> {
            service.findMovieDetails(movieId)
        }
    }

    @Test
    fun shouldReturnMovieDetails() {
        //given
        every { movieRepository.findById(movieId) } returns Optional.of(Movie(movieId, title, imdbId))
        val releaseDate = "2010-10-10"
        val runtime = "105 min"
        val description = "Movie about cars"
        val imdbRating = "7.4"
        every { omdbClient.getMovieDetails(imdbId) } returns OMDbResponse(
            title,
            releaseDate,
            runtime,
            description,
            imdbRating
        )

        //when
        val movieDetails = service.findMovieDetails(movieId)

        //then
        with(movieDetails) {
            assertEquals(movieId, movieId)
            assertEquals(title, movieTitle)
            assertEquals(description, movieDescription)
        }
        assertEquals(runtime, movieDetails.runtime)
        assertEquals(releaseDate, movieDetails.releaseDate)
        assertEquals(imdbId, movieDetails.imdbId)
        assertEquals(BigDecimal(imdbRating), movieDetails.imdbRating)
    }
}