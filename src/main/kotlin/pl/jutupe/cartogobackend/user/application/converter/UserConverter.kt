package pl.jutupe.cartogobackend.user.application.converter

import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.user.application.model.UserResponse
import pl.jutupe.cartogobackend.user.domain.model.User

@Component
class UserConverter {
    fun toResponse(user: User): UserResponse =
        UserResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            avatar = user.avatar,
        )
}