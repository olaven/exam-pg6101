package org.enterprise.exam.api

import com.github.javafaker.Faker
import org.enterprise.exam.shared.dto.remove_these.Room
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Profile("test")
@Component
class TestDataInitializer(

) {

    val faker = Faker()

    @PostConstruct
    fun `add test data`() {


        //TODO: add test data

        /*val directors = (0..3).map {

            directorRepository.save(DirectorEntity(
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
        }*/
    }
}