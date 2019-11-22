package org.enterprise.exam.graphql.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.enterprise.exam.graphql.database.AdvertisementEntity
import org.enterprise.exam.graphql.database.AdvertisementRepository
import org.enterprise.exam.shared.dto.FriendRequestDTO
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class QueryResolver(
        private val advertisementRepository: AdvertisementRepository
) : GraphQLQueryResolver {

    private var descendingAds = false

    @RabbitListener(queues = ["#{queue.name}"])
    fun receiveFromAMQP(friendRequest: FriendRequestDTO) {

        /*
        * If the friend request was between users with the
        * same first letter in email, the ads should be
        * sorted the other way.
        *
        * voteCount -> desc
        * voteCount -> asc
        * */
        if (friendRequest.senderEmail.first() == friendRequest.receiverEmail.first()) {

            descendingAds = !descendingAds
        }
    }


    fun advertisements(): List<AdvertisementEntity> {

        return advertisementRepository.getAdvertisements(3, descendingAds)
    }

}