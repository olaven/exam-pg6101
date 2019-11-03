package org.olaven.enterprise.mock.movies.dto

import org.olaven.enterprise.mock.movies.WrappedResponse

class DirectorDTO (

    val givenName: String,

    val familyName: String,

    val movies: List<MovieDTO>,

    val id: Long? = null
): WrappedResponse<DirectorDTO>()