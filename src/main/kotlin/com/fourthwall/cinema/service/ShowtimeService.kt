package com.fourthwall.cinema.service

import com.fourthwall.cinema.controller.CreateShowtimeRequest
import com.fourthwall.cinema.controller.CreateShowtimeResponse
import com.fourthwall.cinema.controller.UpdateShowtimeRequest
import com.fourthwall.cinema.model.Money
import com.fourthwall.cinema.model.MovieRepository
import com.fourthwall.cinema.model.Showtime
import com.fourthwall.cinema.model.ShowtimeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

interface ShowtimeService {
    fun findShowtimes(movieId: Int, startDate: LocalDate, endDate: LocalDate?): List<Showtime>
    fun findById(showtimeId: Int): Showtime
    fun createShowtime(request: CreateShowtimeRequest): CreateShowtimeResponse
    fun updateShowtime(showtimeId: Int, request: UpdateShowtimeRequest)
}

@Service
class ShowtimeServiceImpl(
    private val showtimeRepository: ShowtimeRepository,
    private val movieRepository: MovieRepository
) : ShowtimeService {
    override fun findShowtimes(movieId: Int, startDate: LocalDate, endDate: LocalDate?): List<Showtime> {
        if (!movieRepository.existsById(movieId))
            throw MovieNotExistsException(movieId)
        endDate?.let { return showtimeRepository.findByMovieIdAndShowDateBetween(movieId, startDate, endDate) }
            ?: return showtimeRepository.findByMovieIdAndShowDateGreaterThanEqual(movieId, startDate)
    }

    override fun findById(showtimeId: Int) =
        showtimeRepository.findById(showtimeId).orElseThrow { ShowtimeNotExistsException(showtimeId) }

    @Transactional
    override fun createShowtime(request: CreateShowtimeRequest): CreateShowtimeResponse {
        val movie = movieRepository.findById(request.movieId)
            .orElseThrow { MovieNotExistsException(request.movieId) }
        val showtime = Showtime(
            movie = movie,
            showDate = request.showtimeDate.toLocalDate(),
            showTime = request.showtimeDate.toLocalTime(),
            price = Money.fromDTO(request.price)
        )
        return CreateShowtimeResponse(showtimeRepository.save(showtime).id!!)
    }

    @Transactional
    override fun updateShowtime(showtimeId: Int, request: UpdateShowtimeRequest) {
        val showtime = showtimeRepository.findById(showtimeId).orElseThrow { ShowtimeNotExistsException(showtimeId) }
        showtimeRepository.save(
            showtime.copy(
                showDate = request.showtimeDate.toLocalDate(),
                showTime = request.showtimeDate.toLocalTime(),
                price = Money.fromDTO(request.price)
            )
        )
    }
}

class ShowtimeNotExistsException(showtimeId: Int) : RuntimeException("No showtime with id=$showtimeId exists")