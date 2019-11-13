package org.olaven.enterprise.mock.movies.repository

import org.olaven.enterprise.mock.movies.entity.MovieEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Repository
interface MovieRepository: CrudRepository<MovieEntity, Long>, CustomMovieRepository

interface CustomMovieRepository {

    fun getNextPage(size: Int, keysetId: Long?): List<MovieEntity>
}

@Transactional
@Repository
class MovieRepositoryImpl(
        private val entityManager: EntityManager
): CustomMovieRepository {

    override fun getNextPage(size: Int, keysetId: Long?): List<MovieEntity> {


        require(!(size < 0 || size > 100)) { "Invalid size: $size. Must be between 0 and 100, inclusive." }

        val query: TypedQuery<MovieEntity> = if (keysetId == null)
            entityManager
                    .createQuery("select movie from MovieEntity movie order by movie.id desc", MovieEntity::class.java)
        else
            entityManager
                    .createQuery("select movie from MovieEntity movie where movie.id < ?1 order by movie.id desc", MovieEntity::class.java)
                    .setParameter(1, keysetId)
        query.maxResults = size

        return query.resultList
    }
}