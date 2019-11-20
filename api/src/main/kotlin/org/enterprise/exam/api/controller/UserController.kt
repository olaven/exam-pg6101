package org.enterprise.exam.api.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.*
import org.enterprise.exam.api.Transformer
import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.api.repository.*
import org.enterprise.exam.shared.dto.MessageDTO
import org.enterprise.exam.shared.dto.UserDTO
import org.enterprise.exam.shared.response.UserResponseDTO
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/users")
@Api("/users", description = "Endpoint for users")
class UserController(
        private val userRepository: UserRepository,
        private val messageRepository: MessageRepository,
        private val friendRequestRepository: FriendRequestRepository,
        private val transformer: Transformer
) {


    @GetMapping
    @ApiOperation("Retreive all users, using pagination")
    @ApiResponses(
            ApiResponse(code = 200, message = "Users are sent.")
    )
    fun getAll(
            @ApiParam("The pagination keyset id")
            @RequestParam("keysetEmail", required = false)
            keysetEmail: String?,
            @ApiParam("Search term on email")
            @RequestParam("searchTerm", required = false)
            searchTerm: String?
    ): WrappedResponse<Page<UserDTO>> {


        val pageSize = 10
        val users = userRepository.getPage(keysetEmail, searchTerm, pageSize)
                .map { transformer.userToDTO(it) }


        val next = if (users.size == pageSize)
            if (searchTerm != null)
                "/users?keysetEmail=${users.last().email}&searchTerm=${searchTerm}"
            else
                "/users?keysetEmail=${users.last().email}"
        else null

        val page = Page(users, next)
        return WrappedResponse(200, page).validated()
    }


    @GetMapping("/{email}/friends")
    @ApiOperation("Get the friends of the user")
    @ApiResponses(
            ApiResponse(code = 200, message = "successfully retrieved friends")
    )
    fun getFriends(
            @ApiParam("The email of the given user")
            @PathVariable("email")
            email: String,
            @ApiParam("The pagination keysetId (i.e. date of last fetched, if any)")
            @RequestParam("keysetId", required = false)
            keysetId: String?
    ): ResponseEntity<WrappedResponse<Page<UserEntity>>> {

        val pageSize = 10
        val friends = userRepository.getFriends(email, keysetId, pageSize)

        val next = if (friends.size == pageSize) {
            "/users/$email/friends?keysetId=${friends.last().email}"
        } else {
            null
        }

        val page = Page(friends, next)
        return ResponseEntity.status(200).body(
                WrappedResponse(200, page).validated()
        )
    }


    @GetMapping("/{email}/timeline")
    @ApiOperation("Get the timeline of the user")
    @ApiResponses(
            ApiResponse(code = 200, message = "successfully retrieved timeline"),
            ApiResponse(code = 403, message = "May only see timeline of friends")
    )
    fun getTimeline(
            @ApiParam("The email of the given user")
            @PathVariable("email")
            email: String,
            @ApiParam("The pagination keyset date (i.e. date of last fetched, if any)")
            @RequestParam("keysetDate", required = false)
            keysetDate: Long?,
            authentication: Authentication
    ): ResponseEntity<WrappedResponse<Page<MessageDTO>>> {

        val areFriends = friendRequestRepository.areFriends(email, authentication.name)

        if (!areFriends && email != authentication.name) return ResponseEntity.status(403).body(
                WrappedResponse<Page<MessageDTO>>(403, null, "You may only view the timeline of yourself, and your friends").validated()
        )

        val pageSize = 10
        val messages = messageRepository.getTimeline(email, keysetDate, pageSize)
                .map { transformer.messageToDto(it) }

        val next = if (messages.size == pageSize) {
            "/users/$email/timeline?keysetDate=${messages.last().creationTime}"
        } else {
            null
        }

        val page = Page(messages, next)
        return ResponseEntity.status(200).body(
                WrappedResponse(200, page).validated()
        )
    }


    @GetMapping("/{email}")
    @ApiOperation("Retrieve a single user")
    @ApiResponses(
            ApiResponse(code = 200, message = "The user was found and retrieved"),
            ApiResponse(code = 404, message = "The user could not be found")
    )
    fun getUser(
            @ApiParam("The email of the user")
            @PathVariable("email")
            email: String
            /*@ApiParam("Whether to retrieve friends of the user")
            @RequestParam("expand", defaultValue = "NONE")
            expand: Expand = Expand.NONE*/
    ): ResponseEntity<WrappedResponse<UserDTO>> {

        val entity = userRepository.findById(email)

        return if (entity.isPresent) {

            val userDTO = transformer.userToDTO(entity.get())
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
    @ApiOperation("Create a user")
    @ApiResponses(
            ApiResponse(code = 201, message = "The userDTO was created"),
            ApiResponse(code = 203, message = "auth-user is not authorized to create this user"),
            ApiResponse(code = 403, message = "You are not allowed to create this user"),
            ApiResponse(code = 400, message = "There was something wrong with the request")
    )
    fun createUser(
            @ApiParam("The userDTO object")
            @RequestBody
            userDTO: UserDTO,
            authentication: Authentication
    ): ResponseEntity<WrappedResponse<UserDTO>> {

        if (authentication.name != userDTO.email) {

            return ResponseEntity.status(403).body(
                    UserResponseDTO(403, null, "You are not allowed to create this user").validated()
            )
        }

        val alreadyPresentUser = userRepository.findById(userDTO.email)
        if (alreadyPresentUser.isPresent) return ResponseEntity.status(409).body(
                UserResponseDTO(409, null, "Data is already registered for this user").validated()
        )

        val entity = transformer.userToEntity(userDTO)

        //NOTE: ConstraintViolation is picked up by exception handlers
        val persisted = userRepository.save(entity)

        return ResponseEntity.created(URI.create("/users/${persisted.email}")).body(
                UserResponseDTO(201, userDTO).validated()
        )
    }


    /*
    * NOTE: This function is inspired by:
    * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/7b9ee145f66718d5976d273b99542a374571b8cf/advanced/rest/patch/src/main/kotlin/org/tsdes/advanced/rest/patch/CounterRest.kt
    * */
    @PatchMapping(path = ["/{email}"],
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
            @ApiParam("The email of the user")
            @PathVariable("email")
            email: String,
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

        if (jsonNode.has("email")) {
            //shouldn't be allowed to modify the counter id
            return ResponseEntity.status(409).body(
                    UserResponseDTO(409, null, "You may not change email").validated()
            )
        }


        val entityOptional = userRepository.findById(email)
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
        * To showcase how JSON merge patch works, I am keeping the
        * code like this.
        * */

        entity.givenName = newGivenName
        entity.familyName = newFamilyName

        //NOTE: constraint violation will be caught by exception handler
        userRepository.save(entity)


        return ResponseEntity.status(204).body(
                UserResponseDTO(204, null).validated()
        )
    }


}