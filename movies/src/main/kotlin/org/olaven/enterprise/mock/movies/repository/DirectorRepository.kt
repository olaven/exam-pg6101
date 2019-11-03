package org.olaven.enterprise.mock.movies.repository

import org.olaven.enterprise.mock.movies.entity.DirectorEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DirectorRepository: CrudRepository<DirectorEntity, Long>