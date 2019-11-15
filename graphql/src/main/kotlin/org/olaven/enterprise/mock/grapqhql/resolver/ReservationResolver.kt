package org.olaven.enterprise.mock.grapqhql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.olaven.enterprise.mock.grapqhql.entity.ReservationEntity
import org.springframework.stereotype.Component

@Component
class ReservationResolver : GraphQLResolver<ReservationEntity>