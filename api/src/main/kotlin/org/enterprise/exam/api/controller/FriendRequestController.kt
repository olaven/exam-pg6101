package org.enterprise.exam.api.controller

import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Api("/users", description = "Endpoint for users")
class FriendRequestController