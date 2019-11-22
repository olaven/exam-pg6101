package org.enterprise.exam.graphql

import com.github.javafaker.Faker
import io.restassured.RestAssured
import org.enterprise.exam.graphql.database.AdvertisementEntity
import org.enterprise.exam.graphql.database.AdvertisementRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GraphQLTestBase{

    @Autowired
    protected lateinit var advertisementRepository: AdvertisementRepository

    @LocalServerPort
    protected var port = 0

    private val faker = Faker()

    @BeforeEach
    fun clean() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/graphql"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        advertisementRepository.deleteAll()
    }



    protected fun persistAdvertisement() = advertisementRepository.save(
            AdvertisementEntity(

                    message = faker.lorem().word(),
                    voteCount = 0
            )
    )
}