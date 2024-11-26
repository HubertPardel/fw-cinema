package com.fourthwall.cinema.model

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository

@Table(name = "movies")
@Entity
class Movie(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null,
    @Column(nullable = false) val title: String = "",
    @Column(name = "imdb_id", nullable = false) val imdbId: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Movie

        if (title != other.title) return false
        if (imdbId != other.imdbId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + imdbId.hashCode()
        return result
    }
}

interface MovieRepository : JpaRepository<Movie, Int>