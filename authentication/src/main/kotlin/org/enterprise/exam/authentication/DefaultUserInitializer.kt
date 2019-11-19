package org.enterprise.exam.authentication

import org.enterprise.exam.authentication.user.UserService
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DefaultUserInitializer(
        private val userService: UserService
) {

    @PostConstruct
    fun addDefaultUsers(){

        userService.createUser("admin@mail.com", "admin", setOf("USER", "ADMIN"))
        userService.createUser("adam@mail.com", "adampass", setOf("USER"))
        userService.createUser("charlie@mail.com", "charliepass", setOf("USER"))
    }
}