package org.enterprise.exam.graphql

import org.enterprise.exam.graphql.database.AdvertisementEntity
import org.enterprise.exam.graphql.database.AdvertisementRepository
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DefaultGraphqlData(
        private val advertisementRepository: AdvertisementRepository
) {

    @PostConstruct
    fun `add test data`() {



        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing soap!"
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing bikes!"
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing strawberries!"
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing soda!"
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing toys!"
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing watches!"
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing cars!"
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing bikes!"
        ))
    }

}