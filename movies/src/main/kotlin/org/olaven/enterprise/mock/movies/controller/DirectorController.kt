package org.olaven.enterprise.mock.movies.controller

import com.google.common.base.Throwables
import io.swagger.annotations.*
import org.olaven.enterprise.mock.movies.Transformer
import org.olaven.enterprise.mock.movies.WrappedResponse
import org.olaven.enterprise.mock.movies.dto.DirectorDTO
import org.olaven.enterprise.mock.movies.dto.DirectorResponseDTO
import org.olaven.enterprise.mock.movies.dto.MovieDTO
import org.olaven.enterprise.mock.movies.dto.MovieResponseDTO
import org.olaven.enterprise.mock.movies.repository.DirectorRepository
import org.olaven.enterprise.mock.movies.repository.MovieRepository
import org.olaven.enterprise.mock.movies.repository.paginatedResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.ConstraintViolationException

@RestController
@RequestMapping("/directors")
@Api("/directors", description = "Endpoint for directors")
class DirectorController(
        private val movieRepository: MovieRepository,
        private val directorRepository: DirectorRepository,
        private val transformer: Transformer
) {


    @ApiOperation("Retrieves every movie")
    @ApiResponses(
            ApiResponse(code = 200, message = "Receiving movies")
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDirectors(
            @ApiParam("The pagination keyset id")
            @RequestParam("keysetId", required = false)
            keysetId: Long?
    ) = paginatedResponse(
            "directors", 10, directorRepository, keysetId) {

            transformer.directorToDTO(it)
        }

    //TODO: generify logic, as very similar to movie
    @ApiOperation("Retrieve specific director")
    @ApiResponses(
            ApiResponse(code = 200, message = "The Director"),
            ApiResponse(code = 404, message = "Director was nto found")
    )
    @GetMapping("/{id}",  produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDirectors(
            @ApiParam("The ID of the Director")
            @PathVariable("id")
            id: Long
    ): ResponseEntity<DirectorResponseDTO> {

        val directorOptional = directorRepository.findById(id)
        if (directorOptional.isPresent) {

            val dto = transformer.directorToDTO(directorOptional.get())
            return ResponseEntity.ok(DirectorResponseDTO(200, dto))
        }

        return ResponseEntity.status(404).body(DirectorResponseDTO(404, null))
    }

    //TODO: generify logic, as very similar to movie
    @ApiOperation("Create a new director resource")
    @ApiResponses(
            ApiResponse(code = 201, message = "Created"),
            ApiResponse(code = 409, message = "Client wrongly tries to decide ID"),
            ApiResponse(code = 400, message = "The director object is likely malformed")
    )
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMovies(
            @ApiParam("The director object")
            @RequestBody directorDTO: DirectorDTO
    ): ResponseEntity<WrappedResponse<DirectorDTO>> {

        if (directorDTO.id != null) {
            return ResponseEntity.status(409).build()
        }

        try {

            val entity = transformer.directorToEntity(directorDTO)
            val persisted = directorRepository.save(entity)

            return ResponseEntity.created(URI.create("/api/movies/${entity.id}")).body(
                    DirectorResponseDTO(201, directorDTO.apply { id = persisted.id.toString() }).validated()
            )

        } catch (exception: Exception) {

            if (Throwables.getRootCause(exception) is ConstraintViolationException)
                return ResponseEntity.badRequest().build()

            throw exception
        }
    }
}