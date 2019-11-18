package org.olaven.enterprise.exam.movies

import com.github.javafaker.Faker
import org.olaven.enterprise.exam.movies.entity.DirectorEntity
import org.olaven.enterprise.exam.movies.entity.MovieEntity
import org.olaven.enterprise.exam.movies.entity.ScreeningEntity
import org.olaven.enterprise.exam.movies.repository.DirectorRepository
import org.olaven.enterprise.exam.movies.repository.MovieRepository
import org.olaven.enterprise.exam.movies.repository.ScreeningRepository
import org.olaven.enterprise.exam.shared.dto.Room
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Profile("test")
@Component
class TestDataInitializer(
        private val directorRepository: DirectorRepository,
        private val movieRepository: MovieRepository,
        private val screeningRepository: ScreeningRepository
) {

    val faker = Faker()

    @PostConstruct
    fun `add test data`() {

        val directors = listOf(
                DirectorEntity(
                        givenName = faker.name().firstName(),
                        familyName = faker.name().lastName()
                ),
                DirectorEntity(
                        givenName = faker.name().firstName(),
                        familyName = faker.name().lastName()
                ),
                DirectorEntity(
                        givenName = faker.name().firstName(),
                        familyName = faker.name().lastName()
                )
        ).map { directorRepository.save(it) }

        val movies = listOf(
                MovieEntity(
                        title = faker.book().title(),
                        year = faker.number().numberBetween(1900, 2020),
                        director = directors.random()
                ),
                MovieEntity(
                        title = faker.book().title(),
                        year = faker.number().numberBetween(1900, 2020),
                        director = directors.random()
                ),
                MovieEntity(
                        title = faker.book().title(),
                        year = faker.number().numberBetween(1900, 2020),
                        director = directors.random()
                ),
                MovieEntity(
                        title = faker.book().title(),
                        year = faker.number().numberBetween(1900, 2020),
                        director = directors.random()
                )
        ).map { movieRepository.save(it) }

        listOf(
                ScreeningEntity(
                        time = ZonedDateTime.from(faker.date().future(44, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault())),
                        movie = movies.random(),
                        room = Room.values().random(),
                        availableTickets = faker.run { number().numberBetween(0, 200) }
                ),
                ScreeningEntity(
                        time = ZonedDateTime.from(faker.date().future(44, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault())),
                        movie = movies.random(),
                        room = Room.values().random(),
                        availableTickets = faker.number().numberBetween(0, 200)
                ),
                ScreeningEntity(
                        time = ZonedDateTime.from(faker.date().future(44, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault())),
                        movie = movies.random(),
                        room = Room.values().random(),
                        availableTickets = faker.number().numberBetween(0, 200)
                ),
                ScreeningEntity(
                        time = ZonedDateTime.from(faker.date().future(44, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault())),
                        movie = movies.random(),
                        room = Room.values().random(),
                        availableTickets = faker.number().numberBetween(0, 200)
                )
        ).map { screeningRepository.save(it) }
    }
}