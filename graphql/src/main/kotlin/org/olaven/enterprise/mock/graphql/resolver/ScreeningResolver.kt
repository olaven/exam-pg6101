package org.olaven.enterprise.mock.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.olaven.enterprise.mock.shared.dto.ScreeningDTO
import org.springframework.stereotype.Component

@Component
class ScreeningResolver : GraphQLResolver<ScreeningDTO> {

    fun time(screening: ScreeningDTO) =
            screening.time.toString()
}
