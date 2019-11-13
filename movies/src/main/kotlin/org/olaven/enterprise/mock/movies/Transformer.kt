package org.olaven.enterprise.mock.movies

import org.olaven.enterprise.mock.movies.dto.DirectorDTO
import org.olaven.enterprise.mock.movies.dto.MovieDTO
import org.olaven.enterprise.mock.movies.dto.ScreeningDTO
import org.olaven.enterprise.mock.movies.entity.DirectorEntity
import org.olaven.enterprise.mock.movies.entity.MovieEntity
import org.olaven.enterprise.mock.movies.entity.ScreeningEntity
import org.olaven.enterprise.mock.movies.repository.DirectorRepository
import org.olaven.enterprise.mock.movies.repository.MovieRepository
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAccessor

@Component
class Transformer(
        private val movieRepository: MovieRepository,
        private val directorRepository: DirectorRepository
) {

    fun movieToDTO(movieEntity: MovieEntity): MovieDTO =
            MovieDTO(
                    movieEntity.title,
                    movieEntity.year,
                    movieEntity.director.id.toString(),
                    movieEntity.id.toString()
            )

    /**
     * @return Entity, `null` if director could not be found
     */
    fun movieToEntity(movieDTO: MovieDTO): MovieEntity {

        val directorOptional = directorRepository.findById(movieDTO.directorID!!.toLong())
        return MovieEntity(movieDTO.title!!, movieDTO.year!!, directorOptional.get(), movieDTO.id?.toLong())
    }

    fun directorToDTO(directorEntity: DirectorEntity) =
            DirectorDTO(
                    givenName = directorEntity.givenName,
                    familyName = directorEntity.familyName,
                    movies = directorEntity.movies.map { movieToDTO(it)},
                    id = directorEntity.id.toString()
            )

    fun directorToEntity(directorDTO: DirectorDTO) = DirectorEntity(
            givenName = directorDTO.givenName,
            familyName = directorDTO.familyName,
            movies = directorDTO.movies.map { movieToEntity(it) }
    )

    fun screeningToDTO(screeningEntity: ScreeningEntity) = ScreeningDTO(
            time = screeningEntity.time.toEpochSecond(),
            movieID = screeningEntity.movie.id.toString(),
            room = screeningEntity.room,
            id = screeningEntity.id.toString()
    )

    fun screeningToEntity(screeningDTO: ScreeningDTO) = ScreeningEntity(
            time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(screeningDTO.time),
                    ZoneId.systemDefault()),
            movie = movieRepository.findById(screeningDTO.id!!.toLong()).get(),
            room = screeningDTO.room,
            id = screeningDTO.id!!.toLong()
    )
}