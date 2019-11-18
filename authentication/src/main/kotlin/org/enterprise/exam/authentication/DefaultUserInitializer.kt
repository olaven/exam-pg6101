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

        userService.createUser("admin", "admin", setOf("ROLE_USER", "ROLE_ADMIN"))
        userService.createUser("adam", "adampass", setOf("ROLE_USER"))
        userService.createUser("charlie", "charliepass", setOf("ROLE_USER"))
    }
}