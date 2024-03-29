package org.enterprise.exam.api

import com.github.javafaker.Faker
import org.enterprise.exam.api.entity.FriendRequestEntity
import org.enterprise.exam.api.entity.MessageEntity
import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.api.repository.FriendRequestRepository
import org.enterprise.exam.api.repository.MessageRepository
import org.enterprise.exam.api.repository.UserRepository
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import kotlin.random.Random


@Component
class DefaultApiData(
    private val userRepository: UserRepository,
    private val friendRequestRepository: FriendRequestRepository,
    private val messageRepository: MessageRepository
) {

    val faker = Faker()

    @PostConstruct
    fun `add test data`() {


        // only add once
        if (!userRepository.findById("admin@mail.com").isPresent) {


            val admin = userRepository.save(UserEntity(
                    "admin@mail.com",
                    "AdminGiven", "AdminFamily"
            ))

            val adam = userRepository.save(UserEntity(
                    "adam@mail.com",
                    "Adam", "Adamson"
            ))

            val charlie = userRepository.save(UserEntity(
                    "charlie@mail.com",
                    "Charlie", "Charlson"
            ))


            //NOTE: this may be the case if running postgres multiple times or similar
            val adminAndCharlieAreFriends = friendRequestRepository.existsBetween(admin.email, charlie.email)
            if (!adminAndCharlieAreFriends) {

                // adding friend request from beginning
                friendRequestRepository.save(FriendRequestEntity(
                        sender = charlie,
                        receiver = admin,
                        status = FriendRequestStatus.PENDING
                ))
            }


            val users = (0..25).map {

                userRepository.save(UserEntity(
                        email = faker.internet().emailAddress(),
                        givenName = faker.name().firstName(),
                        familyName = faker.name().lastName()
                ))
            }.toMutableList().apply {

                addAll(listOf(
                        admin, adam, charlie
                ))
            }

            users.forEach {

                val messageCount = Random.nextInt(1, 25)
                (0 until messageCount).forEach { _ ->

                    messageRepository.save(MessageEntity(
                            sender = it,
                            receiver = it,
                            text = faker.lorem().paragraph(),
                            creationTime = faker.date().past(5, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault())
                    ))
                }
            }

            (0..20).forEach {

                val sender = randomUser(users)
                val receiver = randomUser(users, sender) //avoiding that people make friends with themselves

                /*
                * NOTE: charlie has already sent a request to admin, and we want to avoid duplicates.
                * This is handy for testing purposes.
                * */
                if (!((sender.email == charlie.email) && (receiver.email == admin.email))) {

                    friendRequestRepository.save(FriendRequestEntity(
                            sender = sender,
                            receiver = receiver,
                            status = FriendRequestStatus.values().random()
                    ))
                }
            }
        }
    }

    private fun randomUser(users: List<UserEntity>, not: UserEntity? = null): UserEntity {

        require(users.size > 1) { "users.size has to be >= 2, or else this will cause infinite loop" }

        var user = users.random()
        while (user.email == not?.email) {
            user = users.random()
        }

        return user
    }
}