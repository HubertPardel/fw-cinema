package com.fourthwall.cinema.service

import com.fourthwall.cinema.model.MovieRepository
import com.fourthwall.cinema.model.Showtime
import com.fourthwall.cinema.model.ShowtimeRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

interface ShowtimeService {
    fun findShowtimes(movieId: Int, startDate: LocalDate, endDate: LocalDate?): List<Showtime>
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
}