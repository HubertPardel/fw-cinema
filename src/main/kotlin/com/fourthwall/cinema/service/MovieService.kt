package com.fourthwall.cinema.service

interface MovieService {
    fun getMovieDetails(movieId: Int)
}

class MovieNotExistsException(movieId: Int) : RuntimeException("No movie with id=$movieId exists")