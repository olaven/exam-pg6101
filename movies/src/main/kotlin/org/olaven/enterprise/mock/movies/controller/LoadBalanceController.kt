package org.olaven.enterprise.mock.movies.controller

import io.swagger.annotations.Api
import org.olaven.enterprise.mock.movies.WrappedResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api("Endpoint for testing load balancing")
@RequestMapping("/lb_id")
class LoadBalanceController {

    @GetMapping
    fun getID() = ResponseEntity.ok(
            WrappedResponse(200, System.getenv("LB_ID")).validated()
    )
}