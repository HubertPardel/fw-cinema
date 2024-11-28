package com.fourthwall.cinema.controller

import com.fourthwall.cinema.service.MovieNotExistsException
import com.fourthwall.cinema.service.ReviewAlreadyExists
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.stream.Collectors


@ControllerAdvice
class RestControllerAdvice {

    @ExceptionHandler(value = [MovieNotExistsException::class])
    fun handleMovieNotExistsException(ex: MovieNotExistsException): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [ReviewAlreadyExists::class])
    fun handleReviewAlreadyExistsException(ex: ReviewAlreadyExists): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, List<String?>>> {
        val errors = ex.bindingResult.fieldErrors
            .stream().map { obj: FieldError -> obj.defaultMessage }.collect(Collectors.toList())
        return ResponseEntity(getErrorsMap(errors), HttpHeaders(), HttpStatus.BAD_REQUEST)
    }

    private fun getErrorsMap(errors: List<String?>): Map<String, List<String?>> {
        val errorResponse: MutableMap<String, List<String?>> = HashMap()
        errorResponse["errors"] = errors
        return errorResponse
    }


}