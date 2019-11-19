package org.enterprise.exam.api

import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.api.entity.remove_these.DirectorEntity
import org.enterprise.exam.api.entity.remove_these.MovieEntity
import org.enterprise.exam.api.entity.remove_these.ScreeningEntity
import org.enterprise.exam.api.repository.remove_these.DirectorRepository
import org.enterprise.exam.api.repository.remove_these.MovieRepository
import org.enterprise.exam.shared.dto.UserDTO
import org.enterprise.exam.shared.dto.remove_these.DirectorDTO
import org.enterprise.exam.shared.dto.remove_these.MovieDTO
import org.enterprise.exam.shared.dto.remove_these.ScreeningDTO
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Component
class Transformer(
        private val movieRepository: MovieRepository,
        private val directorRepository: DirectorRepository
) {


    fun userToDTO(user: UserEntity) = UserDTO(
            user.email,
            user.givenName,
            user.familyName,
            user.id.toString()
    )

    fun userToEntity(user: UserDTO) = UserEntity(
            user.email,
            user.givenName,
            user.familyName
    )

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
                    movies = directorEntity.movies.map { movieToDTO(it) },
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
            availableTickets = screeningEntity.availableTickets,
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