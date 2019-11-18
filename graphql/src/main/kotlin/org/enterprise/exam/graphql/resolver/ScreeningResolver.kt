package org.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLResolver
import org.enterprise.exam.shared.dto.ScreeningDTO
import org.springframework.stereotype.Component

@Component
class ScreeningResolver : GraphQLResolver<ScreeningDTO>
