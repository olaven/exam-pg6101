package org.enterprise.exam.graphql.database

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Repository
interface AdvertisementRepository: CrudRepository<AdvertisementEntity, Long>, CustomAdvertisementRepository

interface CustomAdvertisementRepository {

    fun getMostRelevant(userEmail: String, count: Int): MutableList<AdvertisementEntity>
}

@Transactional
open class AdvertisementRepositoryImpl (
        private val entityManager: EntityManager
): CustomAdvertisementRepository {

    override fun getMostRelevant(userEmail: String, count: Int): MutableList<AdvertisementEntity> {

        //TODO: some logic based on given user! (adjusted with amqp)
        val query = entityManager.createQuery("select advertisement from AdvertisementEntity advertisement", AdvertisementEntity::class.java)

        query.maxResults = count
        return query.resultList
    }
}

