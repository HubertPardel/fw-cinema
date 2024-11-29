package com.fourthwall.cinema.model

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Table(name = "movies")
@Entity
data class Movie(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null,
    @Column(nullable = false) val title: String,
    @Column(name = "imdb_id", nullable = false) val imdbId: String
)

@Repository
interface MovieRepository : JpaRepository<Movie, Int>