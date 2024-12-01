package com.fourthwall.cinema.controller

import com.fourthwall.cinema.omdb.OMDbClientException
import com.fourthwall.cinema.service.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.stream.Collectors


@ControllerAdvice
internal class RestControllerAdvice {

    @ExceptionHandler(value = [MovieNotExistsException::class])
    fun handleMovieNotExistsException(ex: MovieNotExistsException): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [MovieDetailsNotAvailableException::class])
    fun handleMovieDetailsNotAvailableException(ex: MovieDetailsNotAvailableException): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [OMDbClientException::class])
    fun handleOMDbClientException(ex: OMDbClientException): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [ReviewAlreadyExistsException::class])
    fun handleReviewAlreadyExistsException(ex: ReviewAlreadyExistsException): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(value = [ReviewNotExistsException::class])
    fun handleReviewNotExistsException(ex: ReviewNotExistsException): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, List<String?>>> {
        val errors = ex.bindingResult.fieldErrors
            .stream().map { obj: FieldError -> obj.defaultMessage }.collect(Collectors.toList())
        return ResponseEntity(getErrorsMap(errors), HttpHeaders(), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [ShowtimeNotExistsException::class])
    fun handleShowtimeNotExistsException(ex: ShowtimeNotExistsException): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    private fun getErrorsMap(errors: List<String?>): Map<String, List<String?>> {
        val errorResponse: MutableMap<String, List<String?>> = HashMap()
        errorResponse["errors"] = errors
        return errorResponse
    }
}