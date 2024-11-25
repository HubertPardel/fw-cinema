package com.fourthwall.cinema.model

import jakarta.persistence.*
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@Table(name = "showtimes")
@Entity
class Showtime(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null,
    @ManyToOne val movie: Movie? = null,
    @Column(name = "show_date", nullable = false) val showDate: LocalDate? = null,
    @Column(name = "show_time", nullable = false) val showTime: LocalTime? = null,
    @AttributeOverrides(
        value = arrayOf(
            AttributeOverride(name = "amount", column = Column(name = "price_amount")),
            AttributeOverride(name = "currency", column = Column(name = "price_currency"))
        )
    ) val price: Money? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Showtime

        if (movie != other.movie) return false
        if (showDate != other.showDate) return false
        if (showTime != other.showTime) return false
        if (price != other.price) return false

        return true
    }

    override fun hashCode(): Int {
        var result = movie?.hashCode() ?: 0
        result = 31 * result + (showDate?.hashCode() ?: 0)
        result = 31 * result + (showTime?.hashCode() ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        return result
    }
}

@Embeddable
data class Money(val amount: BigDecimal? = null, @Enumerated(EnumType.STRING) val currency: Currency? = null)

enum class Currency(val code: String) { EUR("EUR"), PLN("PLN") }

interface ShowtimeRepository : PagingAndSortingRepository<Showtime, Int>

