package org.enterprise.exam.api.repository

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.jetbrains.annotations.NotNull
import org.enterprise.exam.shared.dto.BaseDTO
import org.enterprise.exam.api.entity.BaseEntity
import org.enterprise.exam.shared.response.WrappedResponse
import org.springframework.http.ResponseEntity
import javax.persistence.TypedQuery

//TODO: move this to shared?
@ApiModel(description = "Paginated resources, using keyset pagination")
class Page<T>(

        @ApiModelProperty("The data contained in page")
        @get:NotNull
        var list: List<T> = listOf(),

        @ApiModelProperty("Link to the next page, if it exists")
        var next: String? = null
)

//TODO: Delete this file / content below?


fun <Entity : BaseEntity, DTO : BaseDTO> paginatedResponse(
        path: String,
        pageSize: Int,
        repository: PaginatedRepository<Entity>, keysetId: Any?,
        transform: (entity: Entity) -> DTO,
        getPrimaryKey: (dto: DTO) -> String): ResponseEntity<WrappedResponse<Page<DTO>>> {

    val retrieved = repository
            .getNextPage(pageSize, keysetId)
            .map { transform(it) }

    val nextLocation =
            if (retrieved.count() == pageSize)
                "/$path?keysetId=${getPrimaryKey(retrieved.last())}"
            else null

    val page = Page(retrieved, nextLocation)
    return ResponseEntity.ok(WrappedResponse(200, data = page).validated())
}

internal inline fun <reified T> generalGetNextPage(
        keysetId: Any?,
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
    fun getNextPage(size: Int, keysetId: Any?): List<T>
}