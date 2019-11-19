package org.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.enterprise.exam.shared.dto.remove_these.ScreeningDTO
import org.springframework.stereotype.Component

@Component
class ScreeningResolver : GraphQLResolver<ScreeningDTO> {

    fun time(screening: ScreeningDTO) =
            screening.time.toString()
}
