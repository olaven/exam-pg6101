package org.enterprise.exam.api.repository

import org.enterprise.exam.api.entity.MovieEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
interface MovieRepository : CrudRepository<MovieEntity, Long>, PaginatedRepository<MovieEntity>

@Transactional
@Repository
open class MovieRepositoryImpl(
        private val entityManager: EntityManager
) : PaginatedRepository<MovieEntity> {

    override fun getNextPage(size: Int, keysetId: Long?) =
            generalGetNextPage<MovieEntity>(keysetId, size,
                    entityManager.createQuery("select movie from MovieEntity movie order by movie.id desc, movie.year", MovieEntity::class.java),
                    entityManager.createQuery("select movie from MovieEntity movie where movie.id < :keysetId order by movie.id desc, movie.year", MovieEntity::class.java)
            )
}