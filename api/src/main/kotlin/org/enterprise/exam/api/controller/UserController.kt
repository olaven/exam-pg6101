package org.enterprise.exam.api.controller

import io.swagger.annotations.*
import org.enterprise.exam.api.Transformer
import org.enterprise.exam.api.repository.UserRepository
import org.enterprise.exam.shared.dto.UserDTO
import org.enterprise.exam.shared.response.UserResponseDTO
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Api("/users", description = "Endpoint for users")
class UserController(
        private val userRepository: UserRepository,
        private val transformer: Transformer
) {

    enum class Expand {
        NONE, FRIENDS
    }

    // @GetMapping //all

    // @GetMapping //users/id/friends (IS THIS OK REST?)


    @GetMapping("/{id}")
    @ApiOperation("Retrieve a single user")
    @ApiResponses(
            ApiResponse(code = 200, message = "The user was found and retrieved"),
            ApiResponse(code = 404, message = "The user could not be found")
    )
    fun getUser(
            @ApiParam("The ID of the user")
            @PathVariable("id")
            id: Long
            /*@ApiParam("Whether to retrieve friends of the user")
            @RequestParam("expand", defaultValue = "NONE")
            expand: Expand = Expand.NONE*/
    ): ResponseEntity<WrappedResponse<UserDTO>> {

        val entity = userRepository.findById(id)

        return if (entity.isPresent) {

            val userDTO  = transformer.userToDTO(entity.get())
            ResponseEntity.status(200).body(
                    UserResponseDTO(200, userDTO).validated()
            )
        } else {

            ResponseEntity.status(404).body(
                    UserResponseDTO(404, null, "The user could not be found").validated()
            )
        }
    }

}