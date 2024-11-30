package com.fourthwall.cinema.controller

import com.fourthwall.cinema.configuration.SecurityConfig
import com.fourthwall.cinema.model.Movie
import com.fourthwall.cinema.service.MovieService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic


@WebMvcTest(controllers = [MovieController::class])
@Import(SecurityConfig::class)
class MovieControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var movieService: MovieService

    @Test
    fun givenExistingMovie_whenGetRequestAuthenticated_thenReturnMovieResponseWithStatus200() {
        //given
        every { movieService.findById(movieId) } returns sampleMovie()

        //then
        mockMvc.get("/v1/movies/$movieId") {
            with(httpBasic("user", "password"))
        }.andExpectAll {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.movieId") {
                value(movieId)
            }
            jsonPath("$.movieTitle") {
                value(movieTitle)
            }
            jsonPath("$.imdbId") {
                value(imdbId)
            }
        }
    }

    @Test
    fun givenExistingMovie_whenGetRequestNotAuthenticated_thenReturnStatus401() {
        //given
        every { movieService.findById(movieId) } returns sampleMovie()

        //then
        mockMvc.get("/v1/movies/$movieId")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    companion object {
        val movieId = 1
        val movieTitle = "title"
        val imdbId = "tt1234"
        fun sampleMovie() = Movie(movieId, movieTitle, imdbId)
    }
}

