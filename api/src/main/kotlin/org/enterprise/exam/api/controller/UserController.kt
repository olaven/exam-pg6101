package org.enterprise.exam.api.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.*
import org.enterprise.exam.api.Transformer
import org.enterprise.exam.api.repository.UserRepository
import org.enterprise.exam.api.repository.paginatedResponse
import org.enterprise.exam.shared.dto.UserDTO
import org.enterprise.exam.shared.response.UserResponseDTO
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
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

    // TODO@GetMapping //users/id/timeline (IS THIS OK REST?)


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
    @ApiOperation("Create a user")
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
        val persisted = userRepository.save(entity)

        return ResponseEntity.created(URI.create("/users/${persisted.id}")).body(
                UserResponseDTO(201, userDTO.apply { id = persisted.id.toString() }).validated()
        )
    }


    /*
    * NOTE: This function is inspired by:
    * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/7b9ee145f66718d5976d273b99542a374571b8cf/advanced/rest/patch/src/main/kotlin/org/tsdes/advanced/rest/patch/CounterRest.kt
    * */
    @PatchMapping(path = ["/{id}"],
            consumes = ["application/merge-patch+json"])
    @ApiOperation("Update a user")
    @ApiResponses(
            ApiResponse(code = 204, message = "The user was updated"),
            ApiResponse(code = 400, message = "The provided user was malformed"),
            ApiResponse(code = 403, message = "Tried to modify someone else"),
            ApiResponse(code = 404, message = "The user could not be found"),
            ApiResponse(code = 409, message = "Tried to modify the user id or email")
    )
    fun updateUser(
            @ApiParam("The ID of the user")
            @PathVariable("id")
            id: Long?,
            @ApiParam("The DTO representation of the user")
            @RequestBody
            userDTO: String,
            authentication: Authentication
    ): ResponseEntity<WrappedResponse<UserDTO>> {

        val jackson = ObjectMapper()
        val jsonNode: JsonNode

        try {
            jsonNode = jackson.readValue(userDTO, JsonNode::class.java)
        } catch (e: Exception) {
            //Invalid JSON data as input
            return ResponseEntity.status(400).body(
                    UserResponseDTO(400, null, "Invalid user JSON")
            )
        }

        if (jsonNode.has("id") || jsonNode.has("email")) {
            //shouldn't be allowed to modify the counter id
            return ResponseEntity.status(409).body(
                    UserResponseDTO(409, null, "You may not change email or id").validated()
            )
        }


        val entityOptional = userRepository.findById(id)
        if (!entityOptional.isPresent) return ResponseEntity.status(404).body(
                UserResponseDTO(404, null, "This user could not be found").validated()
        )

        val entity = entityOptional.get()

        //The user could also be trying to modify someone else
        if (entity.email != authentication.name) {

            return ResponseEntity.status(403).body(
                    UserResponseDTO(403, null, "You may not modify other users.").validated()
            )
        }

        var newGivenName: String? = entity.givenName
        var newFamilyName: String? = entity.familyName

        if (jsonNode.has("givenName")) {

            val node = jsonNode.get("givenName")
            newGivenName = when {
                node.isNull -> null
                node.isTextual -> node.asText()
                else -> return ResponseEntity.status(400).body(
                        UserResponseDTO(400, null, "givenName was malformed").validated()
                )
            }
        }

        if (jsonNode.has("familyName")) {

            val node = jsonNode.get("familyName")
            newFamilyName = when {
                node.isNull -> null
                node.isTextual -> node.asText()
                else -> return ResponseEntity.status(400).body(
                        UserResponseDTO(400, null, "familyName was malformed").validated()
                )
            }
        }


        /*
        * IMPORTANT:
        * Allowing `null` in JSON merge patch will cause a constraint
        * violation, as it is not allowed in the database.
        *
        * However, the handling of `null` is important in JSON Merge Patch.
        * To showcase how the JSON merge patch works, I am keeping the
        * code like this. //TODO: write assumption in readme after testing
        * */

        entity.givenName = newGivenName
        entity.familyName = newFamilyName

        //NOTE: constraint violation will be caught by exception handler
        userRepository.save(entity)

        return ResponseEntity.status(204).body(
                UserResponseDTO(204, null)
        )
    }


}