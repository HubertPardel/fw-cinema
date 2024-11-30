package com.fourthwall.cinema.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fourthwall.cinema.configuration.SecurityConfig
import com.fourthwall.cinema.service.MovieNotExistsException
import com.fourthwall.cinema.service.ShowtimeService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.time.LocalDateTime


@WebMvcTest(controllers = [ShowtimeController::class])
@Import(SecurityConfig::class)
class ShowtimeControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var showtimeService: ShowtimeService

    private var mapper = ObjectMapper().registerModule(JavaTimeModule())

    @Test
    fun givenUserNotAuthenticated_whenCreatingShowtime_thenReturnStatus401() {
        //given
        every { showtimeService.createShowtime(sampleCreateRequest()) } returns sampleCreateResponse()

        //then
        mockMvc.post("/v1/showtimes") {
            content = mapper.writeValueAsString(sampleCreateRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isUnauthorized() }
        }
    }

    @Test
    fun givenUserNotAuthorized_whenCreatingShowtime_thenReturnStatus403() {
        //given
        every { showtimeService.createShowtime(sampleCreateRequest()) } returns sampleCreateResponse()

        //then
        mockMvc.post("/v1/showtimes") {
            with(httpBasic("user", "password"))
            content = mapper.writeValueAsString(sampleCreateRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isForbidden() }
        }
    }

    @Test
    fun givenUserAuthorized_whenCreatingShowtime_thenReturnCreateResponseAndStatus204() {
        //given
        every { showtimeService.createShowtime(sampleCreateRequest()) } returns sampleCreateResponse()

        //then
        mockMvc.post("/v1/showtimes") {
            with(httpBasic("admin", "admin"))
            content = mapper.writeValueAsString(sampleCreateRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.showtimeId") {
                value(showtimeId)
            }
        }
    }

    @Test
    fun givenUserAuthorizedAndMovieNotExists_whenCreatingShowtime_thenStatus404() {
        //given
        every { showtimeService.createShowtime(sampleCreateRequest()) } throws MovieNotExistsException(movieId)

        //then
        mockMvc.post("/v1/showtimes") {
            with(httpBasic("admin", "admin"))
            content = mapper.writeValueAsString(sampleCreateRequest())
            contentType = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isNotFound() }
        }
    }

    companion object {
        val movieId = 1
        val showtimeDate = LocalDateTime.now()
        val price = MoneyDTO(BigDecimal.TEN, "PLN")
        val showtimeId = 1
        fun sampleCreateRequest() = CreateShowtimeRequest(movieId, showtimeDate, price)
        fun sampleCreateResponse() = CreateShowtimeResponse(showtimeId)
    }
}