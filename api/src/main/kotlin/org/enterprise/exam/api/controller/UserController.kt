package org.enterprise.exam.api.controller

import io.swagger.annotations.*
import org.enterprise.exam.api.Transformer
import org.enterprise.exam.api.repository.UserRepository
import org.enterprise.exam.api.repository.paginatedResponse
import org.enterprise.exam.shared.dto.UserDTO
import org.enterprise.exam.shared.response.UserResponseDTO
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/users")
@Api("/users", description = "Endpoint for users")
class UserController(
        private val userRepository: UserRepository,
        private val transformer: Transformer
) {


    @GetMapping
    @ApiOperation("Retreive all users, using pagination")
    @ApiResponses(
            ApiResponse(code = 200, message = "Users are sent.")
    )
    fun getAll(
            @ApiParam("The pagination keyset id")
            @RequestParam("keysetId", required = false)
            keysetId: Long?
    ) = paginatedResponse("users", 10, userRepository, keysetId) {

        transformer.userToDTO(it)
    }


    // TODO@GetMapping //users/id/friends (IS THIS OK REST?)


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


    @PostMapping
    @PreAuthorize("#userDTO.email == principal.name")
    @ApiOperation("Create a userDTO")
    @ApiResponses(
            ApiResponse(code = 201, message = "The userDTO was created"),
            ApiResponse(code = 400, message = "There was something wrong with the request"),
            ApiResponse(code = 409, message = "Client wrongly tried to decide ID")
    )
    fun createUser(
            @ApiParam("The userDTO object")
            @RequestBody userDTO: UserDTO
    ): ResponseEntity<WrappedResponse<UserDTO>> {

        val alreadyPresentUser = userRepository.findByEmail(userDTO.email)
        if (alreadyPresentUser.isPresent) return ResponseEntity.status(409).body(
                UserResponseDTO(409, null, "Data is already registered for this user").validated()
        )

        if (userDTO.id != null) {
            return ResponseEntity.status(409).body(
                    UserResponseDTO(409, null, "ID of new userDTO must be null").validated()
            )
        }

        val entity = transformer.userToEntity(userDTO)

        //NOTE: ConstraintViolation is picked up by exception handlers
        userRepository.save(entity)

        return ResponseEntity.created(URI.create("/api/api/${entity.id}")).body(
                UserResponseDTO(201, userDTO.apply { id = entity.id.toString() }).validated()
        )
    }


}