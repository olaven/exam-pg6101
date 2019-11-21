package org.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.enterprise.exam.graphql.database.AdvertisementEntity
import org.enterprise.exam.graphql.database.AdvertisementRepository
import org.springframework.stereotype.Component

@Component
class QueryResolver(
        private val advertisementRepository: AdvertisementRepository
) : GraphQLQueryResolver {

    fun advertisementsForUser(userEmail: String): List<AdvertisementEntity> {

        //TODO: some logic based on the user
        return advertisementRepository.getMostRelevant(userEmail, 3)
    }

}