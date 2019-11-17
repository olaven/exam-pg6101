package org.olaven.enterprise.mock.movies.controller

import com.github.javafaker.Faker
import io.restassured.RestAssured
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.olaven.enterprise.mock.movies.MoviesApplication
import org.olaven.enterprise.mock.movies.Transformer
import org.olaven.enterprise.mock.movies.entity.DirectorEntity
import org.olaven.enterprise.mock.movies.entity.MovieEntity
import org.olaven.enterprise.mock.movies.entity.ScreeningEntity
import org.olaven.enterprise.mock.movies.repository.DirectorRepository
import org.olaven.enterprise.mock.movies.repository.MovieRepository
import org.olaven.enterprise.mock.movies.repository.ScreeningRepository
import org.olaven.enterprise.mock.shared.dto.DirectorDTO
import org.olaven.enterprise.mock.shared.dto.MovieDTO
import org.olaven.enterprise.mock.shared.dto.Room
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(MoviesApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ControllerTestBase {

    @Autowired
    protected lateinit var transformer: Transformer
    @Autowired
    private lateinit var moviesRepository: MovieRepository
    @Autowired
    private lateinit var directorRepository: DirectorRepository
    @Autowired
    private lateinit var screeningRepository: ScreeningRepository

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

        screeningRepository.deleteAll()
        moviesRepository.deleteAll()
        directorRepository.deleteAll()
    }

    protected fun authenticated(username: String, password: String): RequestSpecification = RestAssured.given()
            .auth().basic(username, password)

    protected fun getDummyMovie(directorID: Long): MovieDTO {

        return MovieDTO(
                faker.book().title(),
                faker.number().numberBetween(1900, 2030),
                directorID.toString()
        )
    }

    protected fun getDummyDirector() = DirectorDTO(
            givenName = faker.name().firstName(),
            familyName = faker.name().lastName(),
            movies = emptyList()
    )


    protected fun persistDirectors(count: Int) = (0 until count).forEach {
        persistDirector()
    }

    protected fun persistMovies(count: Int) = (0 until count).forEach {
        persistMovie()
    }

    protected fun persistScreenings(count: Int) = (0 until count).forEach {
        persistScreening()
    }


    protected fun persistDirector(): DirectorEntity {

        val director = DirectorEntity(
                givenName = faker.name().firstName(),
                familyName = faker.name().lastName(),
                movies = emptyList()
        )

        return directorRepository.save(director)
    }


    protected fun persistMovie(director: DirectorEntity = persistDirector()): MovieEntity {

        val movie = MovieEntity(
                title = faker.book().title(),
                year = faker.random().nextInt(1890, 2050),
                director = director
        )

        return moviesRepository.save(movie)
    }

    protected fun persistScreening(movie: MovieEntity = persistMovie()): ScreeningEntity {

        val screening = ScreeningEntity(
                time = ZonedDateTime.from(faker.date().future(40, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault())),
                movie = movie,
                room = Room.values().random()
        )

        return screeningRepository.save(screening)
    }
}