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
                message = "Advertisement for amazing soap!",
                voteCount = 0
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing bikes!",
                voteCount = 10
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing strawberries!",
                voteCount = 6
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing soda!",
                voteCount = 4
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing toys!",
                voteCount = 3
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing watches!",
                voteCount = 7
        ))
        advertisementRepository.save(AdvertisementEntity(
                message = "Advertisement for amazing cars!",
                voteCount = 4
        ))
    }
}