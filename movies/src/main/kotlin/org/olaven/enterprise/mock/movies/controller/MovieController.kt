package org.olaven.enterprise.mock.movies.controller

import com.google.common.base.Throwables
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.bouncycastle.crypto.tls.ContentType
import org.olaven.enterprise.mock.movies.Transformer
import org.olaven.enterprise.mock.movies.dto.MovieDTO
import org.olaven.enterprise.mock.movies.repository.MovieRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.ConstraintViolationException

@RestController
@RequestMapping("/movies")
@Api("/movies", description = "Endpoint for movies")
class MovieController(
        private val movieRepository: MovieRepository,
        private val transformer: Transformer
) {

    @ApiOperation("Retrieves every movie")
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMovies() = movieRepository.findAll()
            .map { transformer.movieToDTO(it) }

    @ApiOperation("Create a new movie resource")
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun postMovie(
            @RequestBody movieDTO: MovieDTO
    ): ResponseEntity<MovieDTO> {

        if (movieDTO.id != null) {
            return ResponseEntity.status(409).build<MovieDTO>()
        }

        try {

            val entity = transformer.movieToEntity(movieDTO)
            movieRepository.save(entity)

            return ResponseEntity.created(URI.create("/api/movies/${entity.id}")).body(movieDTO.apply {
                id = entity.id.toString()
            })
        } catch (exception: Exception) {

            if (Throwables.getRootCause(exception) is ConstraintViolationException)
                return ResponseEntity.badRequest().build()

            throw exception
        }
    }

}