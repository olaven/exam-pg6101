package org.olaven.enterprise.mock.movies.controller

import io.swagger.annotations.*
import org.olaven.enterprise.mock.movies.Transformer
import org.olaven.enterprise.mock.movies.responseDTO.ScreeningResponseDTO
import org.olaven.enterprise.mock.movies.repository.ScreeningRepository
import org.olaven.enterprise.mock.movies.repository.paginatedResponse
import org.olaven.enterprise.mock.shared.WrappedResponse
import org.olaven.enterprise.mock.shared.dto.ScreeningDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/screenings")
@Api("/screenings", description = "Endpoint for screenings")
class ScreeningController(
        private val screeningRepository: ScreeningRepository,
        private val transformer: Transformer
) {

    @ApiOperation("Get multiple screnings")
    @ApiResponses(
            ApiResponse(code = 200, message = "Getting screenings")
    )
    @GetMapping
    fun getScreenings(
            @ApiParam("The pagination keyset id")
            @RequestParam("keysetId", required = false)
            keysetId: Long?
    ) = paginatedResponse("screenings", 20, screeningRepository, keysetId) {

        transformer.screeningToDTO(it)
    }

    @ApiOperation("Get specific screening")
    @ApiResponses(
            ApiResponse(code = 200, message = "Retrieved screening succesfully"),
            ApiResponse(code = 404, message = "Screening was not found")
    )
    @GetMapping("/{id}")
    fun getScreening(
            @ApiParam("The ID of the screening")
            @PathVariable("id", required = true)
            id: Long
    ): ResponseEntity<WrappedResponse<ScreeningDTO>> {

        val entityOptional = screeningRepository.findById(id)
        if (entityOptional.isPresent) {

            val dto = transformer.screeningToDTO(entityOptional.get())
            val wrappedResponse = ScreeningResponseDTO(200, dto).validated()
            return ResponseEntity.ok(wrappedResponse)
        }

        val wrappedResponse = ScreeningResponseDTO(404, null, "Screening was not found").validated()
        return ResponseEntity.status(404).body(wrappedResponse)
    }


}