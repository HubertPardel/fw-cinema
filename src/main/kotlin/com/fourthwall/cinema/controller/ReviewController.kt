package com.fourthwall.cinema.controller

import com.fourthwall.cinema.model.ReviewRepository
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/reviews")
class ReviewController(private val reviewRepository: ReviewRepository) {

    @GetMapping("/all")
    fun findAll(
        @RequestParam(defaultValue = "0") pageNo: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ) = reviewRepository.findAll(PageRequest.of(pageNo, pageSize))
}