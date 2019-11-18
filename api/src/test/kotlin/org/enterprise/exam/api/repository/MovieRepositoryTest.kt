package org.enterprise.exam.api.repository

import org.enterprise.exam.api.repository.DirectorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.enterprise.exam.api.entity.DirectorEntity
import org.enterprise.exam.api.entity.MovieEntity
import org.enterprise.exam.api.repository.MovieRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class MovieRepositoryTest {

    @Autowired
    private lateinit var movieRepository: MovieRepository
    @Autowired
    private lateinit var directorRepository: DirectorRepository

    @Test
    fun `can persist and retrieve movie`() {

        val director = directorRepository.save(DirectorEntity("Stieven", "Spielberg", emptyList()))

        val persisted = movieRepository.save(MovieEntity("Lord of The Rings", 2001, director))
        val retrieved = movieRepository.findById(persisted.id!!).get()

        assertEquals(persisted.id, retrieved.id);
    }
}