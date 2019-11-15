package org.olaven.enterprise.mock.movies.repository

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
): PaginatedRepository<ScreeningEntity> {

    override fun getNextPage(size: Int, keysetId: Long?) =
            generalGetNextPage<ScreeningEntity>(keysetId, size,
                    entityManager.createQuery("select screening from ScreeningEntity screening order by screening.id desc, screening.time", ScreeningEntity::class.java),
                    entityManager.createQuery("select screening from ScreeningEntity screening where screening.id < :keysetId order by screening.id desc, screening.time", ScreeningEntity::class.java)
            )
}