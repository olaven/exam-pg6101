package org.olaven.enterprise.mock.movies

import org.olaven.enterprise.mock.movies.dto.DirectorDTO
import org.olaven.enterprise.mock.movies.dto.MovieDTO
import org.olaven.enterprise.mock.movies.entity.DirectorEntity
import org.olaven.enterprise.mock.movies.entity.MovieEntity
import org.olaven.enterprise.mock.movies.repository.DirectorRepository
import org.olaven.enterprise.mock.movies.repository.MovieRepository
import org.springframework.stereotype.Component

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
                    movies = directorEntity.movies.map { movieToDTO(it) }
            )

    fun directorToEntity(directorDTO: DirectorDTO) = DirectorEntity(
            givenName = directorDTO.givenName,
            familyName = directorDTO.familyName,
            movies = directorDTO.movies.map { movieToEntity(it) }
    )
}