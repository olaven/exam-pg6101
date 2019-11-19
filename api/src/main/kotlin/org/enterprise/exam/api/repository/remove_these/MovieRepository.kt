package org.enterprise.exam.api.repository.remove_these

import org.enterprise.exam.api.entity.remove_these.MovieEntity
import org.enterprise.exam.api.repository.PaginatedRepository
import org.enterprise.exam.api.repository.generalGetNextPage
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
interface MovieRepository : CrudRepository<MovieEntity, Long>, PaginatedRepository<MovieEntity>

@Transactional
@Repository
class MovieRepositoryImpl(
        private val entityManager: EntityManager
) : PaginatedRepository<MovieEntity> {

    override fun getNextPage(size: Int, keysetId: Any?) =
            generalGetNextPage<MovieEntity>(keysetId, size,
                    entityManager.createQuery("select movie from MovieEntity movie order by movie.id desc, movie.year", MovieEntity::class.java),
                    entityManager.createQuery("select movie from MovieEntity movie where movie.id < :keysetId order by movie.id desc, movie.year", MovieEntity::class.java)
            )
}