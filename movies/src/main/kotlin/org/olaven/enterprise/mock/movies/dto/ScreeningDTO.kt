package org.olaven.enterprise.mock.movies.dto

import io.swagger.annotations.ApiModelProperty
import org.olaven.enterprise.mock.movies.entity.Room

class ScreeningDTO (

    @ApiModelProperty("The exact time of the screening, in ms (unix date)")
    val time: Long,

    @ApiModelProperty("ID of the movie being shown")
    var movieID: String,

    @ApiModelProperty("The room where the screening will take place")
    var room: Room,//I may need to make this a string

    id: String
): BaseDTO(id)


/*
*
*    @field:NotNull
        var time: ZonedDateTime,

        @field:NotNull
        @field:ManyToOne
        var movie: MovieEntity,

        @field:Enumerated(value = EnumType.STRING)
        var room: Room,

        @field:Id
        @field:GeneratedValue
        override var id: Long? = null*/