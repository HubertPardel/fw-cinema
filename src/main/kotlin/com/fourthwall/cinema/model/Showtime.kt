package com.fourthwall.cinema.model

import com.fourthwall.cinema.controller.MoneyDTO
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@Table(name = "showtimes")
@Entity
data class Showtime(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null,
    @ManyToOne val movie: Movie,
    @Column(name = "show_date", nullable = false) val showDate: LocalDate,
    @Column(name = "show_time", nullable = false) val showTime: LocalTime,
    @AttributeOverrides(
        value = [AttributeOverride(
            name = "amount",
            column = Column(name = "price_amount")
        ), AttributeOverride(name = "currency", column = Column(name = "price_currency"))]
    ) val price: Money
)

@Embeddable
data class Money(val amount: BigDecimal, @Enumerated(EnumType.STRING) val currency: Currency) {
    companion object {
        fun fromDTO(moneyDTO: MoneyDTO) = Money(moneyDTO.amount, Currency.valueOf(moneyDTO.currency))
    }
}

enum class Currency(val code: String) { EUR("EUR"), PLN("PLN") }

@Repository
interface ShowtimeRepository : JpaRepository<Showtime, Int> {
    fun findByMovieIdAndShowDateGreaterThanEqual(movieId: Int, startDate: LocalDate): List<Showtime>
    fun findByMovieIdAndShowDateBetween(movieId: Int, startDate: LocalDate, endDate: LocalDate): List<Showtime>
}

