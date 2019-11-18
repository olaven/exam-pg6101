package org.enterprise.exam.api

import com.github.javafaker.Faker
import org.enterprise.exam.api.entity.DirectorEntity
import org.enterprise.exam.api.entity.MovieEntity
import org.enterprise.exam.api.entity.ScreeningEntity
import org.enterprise.exam.api.repository.DirectorRepository
import org.enterprise.exam.api.repository.MovieRepository
import org.enterprise.exam.api.repository.ScreeningRepository
import org.enterprise.exam.shared.dto.Room
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

        val directors = (0..3).map {

            directorRepository.save( DirectorEntity(
                    givenName = faker.name().firstName(),
                    familyName = faker.name().lastName()
            ))
        }

        val movies = (0..25).map {

           movieRepository.save(MovieEntity(
                   title = faker.book().title(),
                   year = faker.number().numberBetween(1900, 2020),
                   director = directors.random()
           ))
        }

        val screenings = (0..10).map {

            screeningRepository.save(ScreeningEntity(
                    time = ZonedDateTime.from(faker.date().future(44, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault())),
                    movie = movies.random(),
                    room = Room.values().random(),
                    availableTickets = faker.run { number().numberBetween(0, 200) }
            ))
        }
    }
}