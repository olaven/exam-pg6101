package org.olaven.enterprise.mock.movies.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.olaven.enterprise.mock.movies.repository.MovieRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/movies")
@Api("/movies", description = "Endpoint for movies")
class MovieController(
        private val movieRepository: MovieRepository
) {

    @ApiOperation("Retrieves every movie")
    @GetMapping
    fun getMovies() = movieRepository.findAll().map { "make me a DTO TODO " }
}