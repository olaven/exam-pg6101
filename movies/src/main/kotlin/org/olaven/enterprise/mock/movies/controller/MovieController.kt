package org.olaven.enterprise.mock.movies.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import io.swagger.annotations.*
import org.bouncycastle.crypto.tls.ContentType
import org.olaven.enterprise.mock.movies.Transformer
import org.olaven.enterprise.mock.movies.WrappedResponse
import org.olaven.enterprise.mock.movies.dto.MovieDTO
import org.olaven.enterprise.mock.movies.dto.MovieResponseDTO
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
    fun getMovies(): ResponseEntity<WrappedResponse<List<MovieDTO>>> {

        val movies = movieRepository.findAll()
                .map { MovieDTO(it.title, it.year, it.director.id.toString(), it.id.toString()) }

        return ResponseEntity.ok(WrappedResponse(200, data = movies).validated())
    }

    @ApiOperation("Retrieve specific movie")
    @ApiResponses(
            ApiResponse(code = 200, message = "The movie"),
            ApiResponse(code = 404, message = "Movie was nto found")
    )
    @GetMapping("/{id}",  produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMovie(
            @ApiParam("The ID of the movie")
            @PathVariable("id")
            id: Long
    ): ResponseEntity<MovieResponseDTO> {

        val movieOptional = movieRepository.findById(id)
        if (movieOptional.isPresent) {

            val dto = transformer.movieToDTO(movieOptional.get())
            return ResponseEntity.ok(MovieResponseDTO(200, dto))
        }

        return ResponseEntity.status(404).body(MovieResponseDTO(404, null))
    }


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
    ): ResponseEntity<WrappedResponse<MovieDTO>> {

        if (movieDTO.id != null) {
            return ResponseEntity.status(409).build()
        }

        try {

            val entity = transformer.movieToEntity(movieDTO)
            movieRepository.save(entity)

            return ResponseEntity.created(URI.create("/api/movies/${entity.id}")).body(
                    MovieResponseDTO(201, movieDTO.apply { id = entity.id.toString() }).validated()
            )

        } catch (exception: Exception) {

            if (Throwables.getRootCause(exception) is ConstraintViolationException)
                return ResponseEntity.badRequest().build()

            throw exception
        }
    }

    @ApiOperation("Do a partial update on a movie")
    @ApiResponses(
            ApiResponse(code = 204, message = "The resource was successfully updated"),
            ApiResponse(code = 400, message = "The body was somehow malformed or had invalid values")
    )
    @PatchMapping("/{id}", consumes = ["application/merge-patch+json"])
    fun patchMovie (
            @PathVariable
            @ApiParam("The ID of given movie")
            id: Long,
            @RequestBody
            @ApiParam("The partial movie update")
            partialUpdate: String
    ): ResponseEntity<WrappedResponse<Nothing>> {

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

            val directorID = jsonNode.get("directorID").asLong()
            val directorOptional = directorRepository.findById(directorID)
            if (!directorOptional.isPresent) return ResponseEntity.status(404).body(
                    WrappedResponse(404, null, "Director was not found").validated()
            )

            movieEntity.director = directorOptional.get()
        }

        try {

            movieRepository.save(movieEntity)
            return ResponseEntity.noContent().build()
        } catch (exception: Exception) {

            if (Throwables.getRootCause(exception) is ConstraintViolationException) {

                return ResponseEntity.status(400).body(
                        WrappedResponse(400, null, "Malformed movie object").validated()
                )
            }

            throw exception
        }
    }

    @ApiOperation("Replace a movie")
    @ApiResponses(
            ApiResponse(code = 201, message = "The resource was replaced"),
            ApiResponse(code = 404, message = "Movie was not found")
    )
    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun putMovie(
            @ApiParam("The movie object")
            @RequestBody()
            movieDTO: MovieDTO
    ): ResponseEntity<WrappedResponse<MovieDTO>> {

        val movieOptional = movieRepository.findById(movieDTO.id?.toLong())
        if (!movieOptional.isPresent)
            return ResponseEntity.status(404).body(
                    MovieResponseDTO(404, null).validated()
            )

        val entity = transformer.movieToEntity(movieDTO)
        try {

            movieRepository.save(entity)
            return ResponseEntity.status(204).body(
                    MovieResponseDTO(204, null).validated()
            )
        } catch (exception: Exception) {

            if (Throwables.getRootCause(exception) is ConstraintViolationException)
                return ResponseEntity.status(400).body(
                        MovieResponseDTO(400, null, "Movie appears to be malformed").validated()
                )

            throw exception
        }
    }

}