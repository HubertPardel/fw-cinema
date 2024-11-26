package com.fourthwall.cinema.model

import jakarta.persistence.*

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Table(name = "reviews")
@Entity
data class Review(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null,
    @ManyToOne val movie: Movie,
    @Column(name = "user_email", nullable = false) val userEmail: String,
    @Column(name = "rate", nullable = false) @Enumerated(EnumType.STRING) val userRate: Rating,
)

enum class Rating(val score: Int) {
    VERY_BAD(1), BAD(2), AVERAGE(3), GOOD(4), VERY_GOOD(5)
}

@Repository
interface ReviewRepository : JpaRepository<Review, Int> {
    fun findByMovieId(movieId: Int, pageable: Pageable): Page<Review>
    fun findByMovieIdAndUserEmail(movieId: Int, userEmail: String): Review?
}
