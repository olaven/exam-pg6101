package org.enterprise.exam.api.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api("Endpoint for testing load balancing")
@RequestMapping("api/lb_id")
class LoadBalanceController {

    @ApiOperation("Get ID of current service")
    @GetMapping
    fun getID() = ResponseEntity.ok(
            WrappedResponse(200, System.getenv("MOVIES_LB_ID")).validated()
    )
}