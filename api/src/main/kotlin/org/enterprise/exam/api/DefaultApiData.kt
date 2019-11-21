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


        val users = (0..15).map {

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

        (0..105).map {

            val sender = randomUser(users)
            friendRequestRepository.save(FriendRequestEntity(
                    sender = sender,
                    receiver = randomUser(users, sender), //avoiding that people make friends with themselves
                    status = FriendRequestStatus.values().random()
            ))
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