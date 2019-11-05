package org.olaven.enterprise.mock.movies.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import io.swagger.annotations.*
import org.olaven.enterprise.mock.movies.Transformer
import org.olaven.enterprise.mock.movies.dto.MovieDTO
import org.olaven.enterprise.mock.movies.entity.DirectorEntity
import org.olaven.enterprise.mock.movies.repository.DirectorRepository
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
        private val directorRepository: DirectorRepository,
        private val transformer: Transformer
) {

    @ApiOperation("Retrieves every movie")
    @ApiResponses(
            ApiResponse(code = 200, message = "Receiving movies")
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMovies() = movieRepository.findAll()
            .map { transformer.movieToDTO(it) }

    @ApiOperation("Create a new movie resource")
    @ApiResponses(
            ApiResponse(code = 201, message = "Created"),
            ApiResponse(code = 409, message = "Client wrongly tries to decide ID"),
            ApiResponse(code = 400, message = "The movie object is likely malformed")
    )
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun postMovie(
            @ApiParam("The movie object")
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

    @ApiOperation("Do a partial update on a movie")
    //TODO: add response documentation
    @PatchMapping("(/{id}", consumes = ["application/merge-patch+json"])
    fun patchMovie(
            @PathVariable
            @ApiParam("The ID of given movie")
            id: Long,
            @RequestBody
            @ApiParam("The partial movie update")
            partialUpdate: String
    ): ResponseEntity<Nothing> {

        val jackson = ObjectMapper()
        val jsonNode: JsonNode
        try {
            jsonNode = jackson.readValue(partialUpdate, JsonNode::class.java)
        } catch (e: Exception) {
            //Invalid JSON data as input
            return ResponseEntity.status(400).build()
        }

        val entityOptional = movieRepository.findById(id)
        if (!entityOptional.isPresent)
            return ResponseEntity.notFound().build()

        val movieEntity = entityOptional.get()

        if (jsonNode.has("title")) {


            movieEntity.title = jsonNode.get("title").asText()
        }

        if (jsonNode.has("year")) {

            movieEntity.year = jsonNode.get("year").intValue()
        }

        if (jsonNode.has("directorID")) {

            val directorID = jsonNode.get("directorID").longValue()
            val directorOptional = directorRepository.findById(directorID)
            if (!directorOptional.isPresent) return ResponseEntity.notFound().build()

            movieEntity.director = directorOptional.get()
        }

        try {

            movieRepository.save(movieEntity)
            return ResponseEntity.noContent().build()
        } catch (exception: Exception) {

            if (Throwables.getRootCause(exception) is ConstraintViolationException) {

                return ResponseEntity.status(400).build<Nothing>()
            }

            throw exception
        }
    }

}