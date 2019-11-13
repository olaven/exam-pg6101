package org.olaven.enterprise.mock.movies.repository

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.jetbrains.annotations.NotNull
import org.olaven.enterprise.mock.movies.WrappedResponse
import org.olaven.enterprise.mock.movies.dto.BaseDTO
import org.olaven.enterprise.mock.movies.entity.BaseEntity
import org.springframework.http.ResponseEntity
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@ApiModel(description = "Paginated resources, using keyset pagination")
class Page<T>(

        @ApiModelProperty("The data contained in page")
        @get:NotNull
        var list: List<T> = listOf(),

        @ApiModelProperty("Link to the next page, if it exists")
        var next: String? = null
)


fun<Entity: BaseEntity, DTO: BaseDTO> paginatedResponse(
        path: String,
        sortingProperty: String,
        pageSize: Int,
        repository: PaginatedRepository<Entity>, keysetId: Long?,
        transform: (entity: Entity) -> DTO): ResponseEntity<WrappedResponse<Page<DTO>>> {

    val retrieved = repository
            .getNextPage(pageSize, keysetId, sortingProperty)
            .map { transform(it) }

    val nextLocation =
            if (retrieved.count() == pageSize)
                "/$path?keysetId=${retrieved.last().id}"
            else null

    val page = Page(retrieved, nextLocation)
    return ResponseEntity.ok(WrappedResponse(200, data = page).validated())
}

internal inline fun<reified T> generalGetNextPage(
        entityManager: EntityManager,
        keysetId: Long?,
        size: Int,
        sortProperty: String): List<T> {

    require(!(size < 0 || size > 100)) { "Invalid size: $size. Must be between 0 and 100, inclusive." }

    val query: TypedQuery<T> = if (keysetId == null)
        entityManager
                .createQuery("select entity from ${T::class.simpleName} entity order by entity.id desc, $sortProperty", T::class.java)
    else
        entityManager
                .createQuery("select entity from ${T::class.simpleName} entity where entity.id < ?1 order by entity.id desc, $sortProperty", T::class.java)
                .setParameter(1, keysetId)

    query.maxResults = size

    return query.resultList.toList()
}

interface PaginatedRepository<T>{
    fun getNextPage(size: Int, keysetId: Long?, sortingProperty: String): List<T>
}