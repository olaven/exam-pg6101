package org.olaven.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.google.common.base.Throwables
import com.google.gson.Gson
import graphql.execution.DataFetcherResult
import graphql.servlet.GenericGraphQLError
import org.olaven.enterprise.exam.graphql.database.ReservationEntity
import org.olaven.enterprise.exam.graphql.database.ReservationRepository
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolationException

/*
* This file is a modified version of:
* https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/24838a1318db1dabab79abfb1ebdef10b170112e/advanced/graphql/news-graphql/src/main/kotlin/org/tsdes/advanced/graphql/newsgraphql/resolver/MutationResolver.kt
* */

@Component
class MutationResolver(
        private val reservationRepository: ReservationRepository

) : GraphQLMutationResolver {

    fun create(screeningID: String, username: String, seatCount: Int): DataFetcherResult<String> {

        try {

            val entity = reservationRepository.save(ReservationEntity(
                    screeningID = screeningID.toLong(),
                    username = username,
                    seatCount = seatCount
            ))

            val json = Gson().toJson(entity)
            return DataFetcherResult(json, emptyList())


        } catch (exception: Exception) {

            //NOTE: cannot rely on default exception handling (from shared-module) as that returns wrapped responses, not for gql

            val cause = Throwables.getRootCause(exception)
            val msg = if (cause is ConstraintViolationException) {
                "Violated constraints: ${cause.message}"
            }else {
                "${exception.javaClass}: ${exception.message}"
            }
            return DataFetcherResult<String>(null, listOf(GenericGraphQLError(msg)))
        }
    }
}