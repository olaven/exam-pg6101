package org.olaven.enterprise.mock.movies.dto

import org.olaven.enterprise.mock.movies.WrappedResponse

class MovieDTO (

        val title: String,

        val year: Int,

        val director: DirectorDTO,

        val id: Long? = null
): WrappedResponse<MovieDTO>()