package org.olaven.enterprise.mock.movies.repository

import org.olaven.enterprise.mock.movies.entity.MovieEntity
import org.olaven.enterprise.mock.movies.entity.ScreeningEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
interface ScreeningRepository: CrudRepository<ScreeningEntity, Long>, PaginatedRepository<ScreeningEntity>

@Transactional
@Repository
class ScreeningRepositoryImpl(
        private val entityManager: EntityManager
): PaginatedRepository<MovieEntity> {

    override fun getNextPage(size: Int, keysetId: Long?, sortingProperty: String) =
            generalGetNextPage<MovieEntity>(entityManager, keysetId, size, sortingProperty)
}