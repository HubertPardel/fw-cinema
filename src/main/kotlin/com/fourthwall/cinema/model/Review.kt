package com.fourthwall.cinema.model

import jakarta.persistence.*
import org.springframework.data.repository.PagingAndSortingRepository

@Table(name = "reviews")
@Entity
class Review(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null,
    @ManyToOne val movie: Movie? = null,
    @Column(name = "user_email", nullable = false) val userEmail: String? = null,
    @Column(name = "rate", nullable = false) @Enumerated(EnumType.STRING) val userRate: Rating? = null,
)

enum class Rating(val score: Int) {
    VERY_BAD(1), BAD(2), AVERAGE(3), GOOD(4), VERY_GOOD(5)
}

interface ReviewRepository : PagingAndSortingRepository<Review, Int>
