package org.olaven.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.olaven.enterprise.exam.shared.dto.ScreeningDTO
import org.springframework.stereotype.Component

@Component
class ScreeningResolver : GraphQLResolver<ScreeningDTO>
