package org.olaven.enterprise.mock.movies.repository

import org.olaven.enterprise.mock.movies.entity.MovieEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
interface MovieRepository: CrudRepository<MovieEntity, Long>, PaginatedRepository<MovieEntity>

@Transactional
@Repository
class MovieRepositoryImpl(
        private val entityManager: EntityManager
): PaginatedRepository<MovieEntity> {

    override fun getNextPage(size: Int, keysetId: Long?) =
            generalGetNextPage<MovieEntity>(entityManager, keysetId, size)
}