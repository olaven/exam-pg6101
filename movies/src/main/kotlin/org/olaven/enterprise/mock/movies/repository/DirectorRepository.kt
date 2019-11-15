package org.olaven.enterprise.mock.movies.repository

import org.olaven.enterprise.mock.movies.entity.DirectorEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
interface DirectorRepository: CrudRepository<DirectorEntity, Long>, PaginatedRepository<DirectorEntity>

@Transactional
@Repository
class DirectorRepositoryImpl(
        private val entityManager: EntityManager
): PaginatedRepository<DirectorEntity> {

    override fun getNextPage(size: Int, keysetId: Long?) =
            generalGetNextPage<DirectorEntity>(keysetId, size,
                entityManager.createQuery("select director from DirectorEntity director order by director.id desc, director.givenName", DirectorEntity::class.java),
                entityManager.createQuery("select director from DirectorEntity director where director.id < :keysetId order by director.id desc, director.givenName", DirectorEntity::class.java)
            )
}