package org.enterprise.exam.api

import org.enterprise.exam.api.entity.FriendRequestEntity
import org.enterprise.exam.api.entity.MessageEntity
import org.enterprise.exam.api.entity.UserEntity
import org.enterprise.exam.api.entity.remove_these.DirectorEntity
import org.enterprise.exam.api.entity.remove_these.MovieEntity
import org.enterprise.exam.api.entity.remove_these.ScreeningEntity
import org.enterprise.exam.api.repository.UserRepository
import org.enterprise.exam.api.repository.remove_these.DirectorRepository
import org.enterprise.exam.api.repository.remove_these.MovieRepository
import org.enterprise.exam.shared.dto.FriendRequestDTO
import org.enterprise.exam.shared.dto.MessageDTO
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
        private val userRepository: UserRepository,
        //TODO: remove these
        private val movieRepository: MovieRepository,
        private val directorRepository: DirectorRepository
) {


    fun userToDTO(user: UserEntity) = UserDTO(
            user.email,
            user.givenName,
            user.familyName
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

    fun messageToEntity(messageDTO: MessageDTO) = MessageEntity(
            text = messageDTO.text,
            creationTime = Instant.ofEpochMilli(messageDTO.creationDate).atZone(ZoneId.systemDefault()),
            sender = userRepository.findById(messageDTO.senderEmail).get(),
            receiver = userRepository.findById(messageDTO.receiverEmail).get()
            //id = messageDTO.id.toLong() //TODO :does it crash when addignthis?
    )

    fun messageToDto(messageEntity: MessageEntity) = MessageDTO(
            text = messageEntity.text,
            creationDate = messageEntity.creationTime.toEpochSecond(),
            receiverEmail = messageEntity.receiver.email,
            senderEmail = messageEntity.sender.email,
            id = messageEntity.id.toString()
    )

    fun friendRequestToDTO(friendRequest: FriendRequestEntity) = FriendRequestDTO(
            senderEmail = friendRequest.sender.email,
            receiverEmail = friendRequest.receiver.email,
            status = friendRequest.status,
            id = friendRequest.id.toString()
    )

    fun friendRequestToEntity(friendRequestDTO: FriendRequestDTO) = FriendRequestEntity(
            sender = userRepository.findById(friendRequestDTO.senderEmail).get(),
            receiver = userRepository.findById(friendRequestDTO.receiverEmail).get(),
            status = friendRequestDTO.status,
            id = friendRequestDTO.id?.toLong()
    )
}