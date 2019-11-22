package org.enterprise.exam.graphql.database

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Repository
interface AdvertisementRepository: CrudRepository<AdvertisementEntity, Long>, CustomAdvertisementRepository

interface CustomAdvertisementRepository {

    fun getAdvertisements(count: Int, descending: Boolean): MutableList<AdvertisementEntity>
}

@Transactional
open class AdvertisementRepositoryImpl (
        private val entityManager: EntityManager
): CustomAdvertisementRepository {

    override fun getAdvertisements(count: Int, descending: Boolean): MutableList<AdvertisementEntity> {

        val query = if (descending)
            entityManager.createQuery("select advertisement from AdvertisementEntity advertisement order by advertisement.voteCount desc", AdvertisementEntity::class.java)
        else
            entityManager.createQuery("select advertisement from AdvertisementEntity advertisement order by advertisement.voteCount asc", AdvertisementEntity::class.java)

        query.maxResults = count
        return query.resultList
    }
}

