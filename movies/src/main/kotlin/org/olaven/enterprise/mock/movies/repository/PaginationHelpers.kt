package org.olaven.enterprise.mock.movies.repository

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.jetbrains.annotations.NotNull
import org.olaven.enterprise.mock.shared.dto.BaseDTO
import org.olaven.enterprise.mock.movies.entity.BaseEntity
import org.olaven.enterprise.mock.shared.WrappedResponse
import org.springframework.http.ResponseEntity
import javax.persistence.TypedQuery

@ApiModel(description = "Paginated resources, using keyset pagination")
class Page<T>(

        @ApiModelProperty("The data contained in page")
        @get:NotNull
        var list: List<T> = listOf(),

        @ApiModelProperty("Link to the next page, if it exists")
        var next: String? = null
)


fun <Entity : BaseEntity, DTO : BaseDTO> paginatedResponse(
        path: String,
        pageSize: Int,
        repository: PaginatedRepository<Entity>, keysetId: Long?,
        transform: (entity: Entity) -> DTO): ResponseEntity<WrappedResponse<Page<DTO>>> {

    val retrieved = repository
            .getNextPage(pageSize, keysetId)
            .map { transform(it) }

    val nextLocation =
            if (retrieved.count() == pageSize)
                "/$path?keysetId=${retrieved.last().id}"
            else null

    val page = Page(retrieved, nextLocation)
    return ResponseEntity.ok(WrappedResponse(200, data = page).validated())
}

internal inline fun <reified T> generalGetNextPage(
        keysetId: Long?,
        size: Int,
        standardQuery: TypedQuery<T>,
        keysetQuery: TypedQuery<T>): List<T> {

    require(!(size < 0 || size > 100)) { "Invalid size: $size. Must be between 0 and 100, inclusive." }

    val query = if (keysetId == null) {

        standardQuery
    } else {

        keysetQuery.setParameter("keysetId", keysetId)
    }

    query.maxResults = size
    return query.resultList.toList()
}

interface PaginatedRepository<T> {
    fun getNextPage(size: Int, keysetId: Long?): List<T>
}