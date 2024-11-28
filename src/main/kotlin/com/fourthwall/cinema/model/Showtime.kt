package com.fourthwall.cinema.model

import jakarta.persistence.*
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
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
        value = arrayOf(
            AttributeOverride(name = "amount", column = Column(name = "price_amount")),
            AttributeOverride(name = "currency", column = Column(name = "price_currency"))
        )
    ) val price: Money
)

@Embeddable
data class Money(val amount: BigDecimal? = null, @Enumerated(EnumType.STRING) val currency: Currency? = null)

enum class Currency(val code: String) { EUR("EUR"), PLN("PLN") }

interface ShowtimeRepository : PagingAndSortingRepository<Showtime, Int>

