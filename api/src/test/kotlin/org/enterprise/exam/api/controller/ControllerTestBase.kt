package org.enterprise.exam.api.controller

import com.github.javafaker.Faker
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.enterprise.exam.api.ApiApplication
import org.enterprise.exam.api.Transformer
import org.enterprise.exam.api.WebSecurityConfigLocalFake
import org.enterprise.exam.api.entity.FriendRequestEntity
import org.enterprise.exam.api.entity.MessageEntity
import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.api.repository.FriendRequestRepository
import org.enterprise.exam.api.repository.MessageRepository
import org.enterprise.exam.api.repository.UserRepository
import org.enterprise.exam.shared.dto.FriendRequestDTO
import org.enterprise.exam.shared.dto.FriendRequestStatus
import org.enterprise.exam.shared.dto.MessageDTO
import org.enterprise.exam.shared.dto.UserDTO
import org.enterprise.exam.shared.dto.remove_these.DirectorDTO
import org.enterprise.exam.shared.dto.remove_these.MovieDTO
import org.enterprise.exam.shared.dto.remove_these.Room
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(ApiApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ControllerTestBase {

    @Autowired
    protected lateinit var transformer: Transformer

    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var messageRepository: MessageRepository
    @Autowired
    private lateinit var friendRequestRepository: FriendRequestRepository


    private val faker = Faker()

    @LocalServerPort
    protected var port = 0

    @BeforeEach
    fun `initialize controller test`() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = ""
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        friendRequestRepository.deleteAll()
        messageRepository.deleteAll()
        userRepository.deleteAll()
    }

    protected fun authenticated(username: String, password: String): RequestSpecification = given()
                .auth().basic(username, password)


    protected fun getDummyUser(user: WebSecurityConfigLocalFake.Companion.TestUser? = null) = UserDTO(
            givenName = faker.name().firstName(),
            familyName = faker.name().lastName(),
            email = user?.email ?: faker.internet().emailAddress()
    )

    protected fun getDummyMessage(senderID: String, receiverID: String) = MessageDTO(
            text = faker.lorem().paragraph(),
            senderEmail = senderID,
            receiverEmail = receiverID,
            creationTime = faker.date().past(3, TimeUnit.HOURS).toInstant().toEpochMilli(),
            id = null
    )

    protected fun persistUsers(count: Int) {

        (0 until count).forEach {

            userRepository.save(
                    UserEntity(
                            givenName = faker.name().firstName(),
                            familyName = faker.name().lastName(),
                            email = faker.internet().emailAddress()
                    )
            )
        }
    }

    protected fun persistUser(user: WebSecurityConfigLocalFake.Companion.TestUser? = null, dto: UserDTO = getDummyUser(user)) = userRepository.save(
            UserEntity(
                    dto.email,
                    dto.givenName,
                    dto.familyName
            )
    )

    protected fun persistMessage(sender: WebSecurityConfigLocalFake.Companion.TestUser, receiver: WebSecurityConfigLocalFake.Companion.TestUser) =
            messageRepository.save(MessageEntity(
                    text = faker.lorem().paragraph(),
                    sender = userRepository.findById(sender.email).get(),
                    receiver = userRepository.findById(receiver.email).get(),
                    creationTime = ZonedDateTime.now()
            ))

    protected fun persistFriendRequest(
            senderEmail: String,
            receiverEmail: String,
            status: FriendRequestStatus = FriendRequestStatus.PENDING
    ) =
            friendRequestRepository.save(FriendRequestEntity(
                    sender = userRepository.findById(senderEmail).get(),
                    receiver = userRepository.findById(receiverEmail).get(),
                    status = status
            ))

    protected fun getFriendRequestDTO(sender: WebSecurityConfigLocalFake.Companion.TestUser, receiver: WebSecurityConfigLocalFake.Companion.TestUser) = FriendRequestDTO(
            senderEmail = sender.email,
            receiverEmail = receiver.email
    )

    protected fun persistFriendRequests(count: Int, sender: WebSecurityConfigLocalFake.Companion.TestUser, receiver: WebSecurityConfigLocalFake.Companion.TestUser) {

        (0 until count).forEach {

            persistFriendRequest(sender.email, receiver.email)
        }
    }

    protected fun postMessage(message: MessageDTO, user: WebSecurityConfigLocalFake.Companion.TestUser): ValidatableResponse =
            authenticated(user.email, user.password)
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(message)
                    .post("/messages")
                    .then()
}