package org.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.google.common.base.Throwables
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import org.enterprise.exam.graphql.database.AdvertisementEntity
import org.enterprise.exam.graphql.database.AdvertisementRepository
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolationException

/*
* This file is a modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/24838a1318db1dabab79abfb1ebdef10b170112e/advanced/graphql/news-graphql/src/main/kotlin/org/tsdes/advanced/graphql/newsgraphql/resolver/MutationResolver.kt
* */

@Component
class MutationResolver(
        private val advertisementRepository: AdvertisementRepository
) : GraphQLMutationResolver {


    fun voteUpAdvertisement(advertisementID: String) = mutateAdvertisement(advertisementID) {

        it.voteCount++
        it
    }

    fun voteDownAdvertisement(advertisementID: String) = mutateAdvertisement(advertisementID) {

        if (it.voteCount == 0) it
        else it.apply { voteCount -= 1 }
    }


    private fun mutateAdvertisement(advertisementID: String, mutation: (advertisementEntity: AdvertisementEntity) -> AdvertisementEntity) = handledAction {

        val convertedID = advertisementID.toLong()
        var advertisement = advertisementRepository.findById(convertedID).get()

        advertisement = mutation(advertisement)

        val updated = advertisementRepository.save(advertisement)
        DataFetcherResult(updated, emptyList())
    }

    private fun handledAction(action: () -> DataFetcherResult<AdvertisementEntity>): DataFetcherResult<AdvertisementEntity> {

        try {

            return action()

        } catch (exception: Exception) {

            //NOTE: cannot rely on default exception handling (from shared-module) as that returns wrapped responses, not for gql
            val cause = Throwables.getRootCause(exception)
            val msg = if (cause is ConstraintViolationException) {
                "Violated constraints: ${cause.message}"
            } else {
                "${exception.javaClass}: ${exception.message}"
            }

            return DataFetcherResult<AdvertisementEntity>(null, listOf(GenericGraphQLError(msg)))
        }
    }
}