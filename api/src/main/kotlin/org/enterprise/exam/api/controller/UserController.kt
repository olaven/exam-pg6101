package org.enterprise.exam.api.controller

import io.swagger.annotations.*
import org.enterprise.exam.api.repository.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Api("/users", description = "Endpoint for users")
class UserController(
        private val userRepository: UserRepository
) {

    // @GetMapping //all


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
    ) {

        val userOptional = userRepository.findById(id)
        //TODO: return after creating DTO
    }

}