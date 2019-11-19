package org.enterprise.exam.api.controller.remove_these

import io.swagger.annotations.*
import org.enterprise.exam.api.Transformer
import org.enterprise.exam.api.repository.paginatedResponse
import org.enterprise.exam.api.repository.remove_these.ScreeningRepository
import org.enterprise.exam.shared.dto.remove_these.ScreeningDTO
import org.enterprise.exam.shared.response.WrappedResponse
import org.enterprise.exam.shared.response.remove_these.ScreeningResponseDTO
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
    ) = paginatedResponse("screenings", 20, screeningRepository, keysetId, {

        transformer.screeningToDTO(it)
    }) {
        it.id.toString()
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