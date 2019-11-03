package org.olaven.enterprise.mock.movies.repository

import org.olaven.enterprise.mock.movies.entity.MovieEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository: CrudRepository<MovieEntity, Long>