package org.olaven.enterprise.mock.shared.dto

import io.swagger.annotations.ApiModelProperty

enum class Room {
        A1, A2, A3,
        B1, B2, B3,
        C1, C2, C3,
        D1, D2, D3,
}

class ScreeningDTO(

        @ApiModelProperty("The exact time of the screening, in ms (unix date)")
        val time: Long,

        @ApiModelProperty("ID of the movie being shown")
        var movieID: String,

        @ApiModelProperty("The room where the screening will take place")
        var room: Room,//I may need to make this a string

        id: String
) : BaseDTO(id)