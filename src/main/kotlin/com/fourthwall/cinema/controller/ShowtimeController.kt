package com.fourthwall.cinema.controller

import com.fourthwall.cinema.model.Money
import com.fourthwall.cinema.model.Showtime
import com.fourthwall.cinema.service.ShowtimeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RestController
@RequestMapping("/v1/showtimes")
internal class ShowtimeController(private val showtimeService: ShowtimeService) {

    @Operation(summary = "Get showtime by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(responseCode = "404", description = "Showtime with given id not found")
        ]
    )
    @GetMapping("/{showtimeId}")
    fun getShowtime(@PathVariable showtimeId: Int) =
        ResponseEntity.status(HttpStatus.OK)
            .body(GetShowtimeResponse.fromShowtime(showtimeService.findById(showtimeId)))

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

    @Operation(summary = "Creates new showtime")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Successful operation"),
            ApiResponse(responseCode = "404", description = "Movie with given id not found")
        ]
    )
    @PostMapping
    fun createShowtime(@RequestBody @Valid request: CreateShowtimeRequest): ResponseEntity<CreateShowtimeResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(showtimeService.createShowtime(request))


    @Operation(summary = "Updates showtime")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Successful operation"),
            ApiResponse(responseCode = "404", description = "Showtime with given id not found")
        ]
    )
    @PutMapping("/{showtimeId}")
    fun updateShowtime(
        @RequestBody @Valid request: UpdateShowtimeRequest,
        @PathVariable showtimeId: Int
    ): ResponseEntity<Any> {
        showtimeService.updateShowtime(showtimeId, request)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

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
                    it.value.map { ShowtimeDetails(it.id!!, it.showTime, Price.fromMoney(it.price)) }.toSet()
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
        description = "Showtime id",
        example = "1",
        type = "int"
    )
    val showtimeId: Int,
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
    ) val price: Price
)

@Schema(description = "Showtime price")
data class Price(val price: String) {
    companion object {
        fun fromMoney(money: Money) = Price("${money.amount} ${money.currency}")
    }
}

@Schema(description = "Request for showtime creation")
data class CreateShowtimeRequest(
    @field:NotNull(message = "Movie id is required")
    @field:Schema(description = "Movie Id", example = "1", type = "int") val movieId: Int,
    @field:NotNull(message = "Showtime date is required")
    @field:Schema(description = "Showtime date", example = "1", type = "string", format = "date-time")
    val showtimeDate: LocalDateTime,
    @field:NotNull(message = "Price is required")
    @field:Schema(
        description = "Price of the show",
    )
    val price: MoneyDTO
)

@Schema(description = "Response for showtime creation")
data class CreateShowtimeResponse(val showtimeId: Int)

@Schema(description = "Represents money amount")
data class MoneyDTO(
    @field:Schema(
        description = "Price amount",
        example = "20.00",
        type = "number",
        format = "double"
    )
    @field:NotNull(message = "Amount is required")
    val amount: BigDecimal, @field:Schema(
        description = "Price currency",
        example = "PLN",

        allowableValues = arrayOf("PLN", "EUR")
    )
    @field:NotNull(message = "Currency is required")
    val currency: String
) {
    companion object {
        fun fromMoney(money: Money) = MoneyDTO(money.amount, money.currency.code)
    }
}

@Schema(description = "Request for showtime update")
data class UpdateShowtimeRequest(
    @field:NotNull(message = "Showtime date is required")
    @field:Schema(description = "Showtime date", example = "1", type = "string", format = "date-time")
    val showtimeDate: LocalDateTime,
    @field:NotNull(message = "Price is required")
    @field:Schema(
        description = "Price of the show",
    )
    val price: MoneyDTO
)

@Schema(description = "Response for getting showtime")
data class GetShowtimeResponse(
    @field:Schema(
        description = "Showtime id",
        example = "1",
        type = "int"
    ) val showtimeId: Int,
    @field:Schema(
        description = "Movie title",
        example = "The Fast and the Furious",
        type = "string"
    ) val movieTitle: String,
    @field:Schema(
        description = "Showtime date and time",
        example = "2024-12-10 16:00:00",
        type = "string",
        format = "date-time"
    ) val showtimeDate: LocalDateTime,
    @field:Schema(
        description = "Price of the show",
    )
    val price: MoneyDTO
) {
    companion object {
        fun fromShowtime(showtime: Showtime) = GetShowtimeResponse(
            showtime.id!!,
            showtime.movie.title,
            LocalDateTime.of(showtime.showDate, showtime.showTime), MoneyDTO.fromMoney(showtime.price)
        )
    }
}