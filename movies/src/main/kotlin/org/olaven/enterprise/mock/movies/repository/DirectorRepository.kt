package org.olaven.enterprise.mock.movies.repository

import org.olaven.enterprise.mock.movies.entity.DirectorEntity
import org.olaven.enterprise.mock.movies.entity.MovieEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Repository
interface DirectorRepository: CrudRepository<DirectorEntity, Long>, PaginatedRepository<DirectorEntity>

@Transactional
@Repository
class DirectorRepositoryImpl(
        private val entityManager: EntityManager
): PaginatedRepository<DirectorEntity> {

    override fun getNextPage(size: Int, keysetId: Long?) =
            generalGetNextPage<DirectorEntity>(entityManager, keysetId, size)
}