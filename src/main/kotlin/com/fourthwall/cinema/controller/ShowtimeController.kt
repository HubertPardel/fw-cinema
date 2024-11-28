package com.fourthwall.cinema.controller

import com.fourthwall.cinema.model.Money
import com.fourthwall.cinema.model.Showtime
import com.fourthwall.cinema.service.ShowtimeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalTime


@RestController
@RequestMapping("/v1/showtimes")
internal class ShowtimeController(private val showtimeService: ShowtimeService) {

    @Operation(summary = "Returns all showtimes for given movie and date range")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "404", description = "Movie with given id not found")
        ]
    )
    @GetMapping
    fun getByMovieId(
        @RequestParam movieId: Int,
        @RequestParam fromDate: LocalDate = LocalDate.now(),
        @RequestParam(required = false) toDate: LocalDate?
    ) = GetShowtimesResponse.fromShowtimes(movieId, showtimeService.findShowtimes(movieId, fromDate, toDate))

}

@Schema(description = "Response for getting movie showtimes")
data class GetShowtimesResponse(
    @field:Schema(
        description = "Movie id",
        example = "1",
        type = "int"
    ) val movieId: Int,
    @field:ArraySchema(schema = Schema(description = "Set of daily schedules")) val dailySchedule: Set<DailySchedule>
) {
    companion object {
        fun fromShowtimes(movieId: Int, showtimes: List<Showtime>) = GetShowtimesResponse(
            movieId,
            showtimes.groupBy { it.showDate }.map { it ->
                DailySchedule(
                    it.key,
                    it.value.map { ShowtimeDetails(it.showTime, MoneyDTO.fromMoney(it.price)) }.toSet()
                )
            }.toSet()
        )

    }
}

@Schema(description = "Daily schedule for the given movie")
data class DailySchedule(
    @field:Schema(
        description = "Date for the schedule",
        example = "2024-12-01",
        type = "string",
        format = "date"
    ) val date: LocalDate,
    @field:ArraySchema(schema = Schema(description = "Hourly schedule for given date")) val hourlySchedule: Set<ShowtimeDetails>
)

@Schema(description = "Showtime time and price")
data class ShowtimeDetails(
    @field:Schema(
        description = "Time of the show",
        example = "16:00:00",
        type = "string",
        format = "time"
    ) val time: LocalTime,
    @field:Schema(
        description = "Price of the show",
        example = "15 PLN",
        type = "string"
    ) val price: MoneyDTO
)

@Schema(description = "Represents money")
data class MoneyDTO(val price: String) {
    companion object {
        fun fromMoney(money: Money) = MoneyDTO("${money.amount} ${money.currency}")
    }
}