package org.enterprise.exam.graphql.database

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AdvertisementRepository: CrudRepository<AdvertisementEntity, Long>