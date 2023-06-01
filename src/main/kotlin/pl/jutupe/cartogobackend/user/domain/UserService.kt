package pl.jutupe.cartogobackend.user.domain

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import org.springframework.stereotype.Service
import pl.jutupe.cartogobackend.user.domain.model.User
import pl.jutupe.cartogobackend.user.infrastructure.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun create(token: GoogleIdToken): User {
        val user = User(
            googleId = token.payload.subject,
            name = token.payload["name"] as String,
            email = token.payload["email"] as String,
            avatar = token.payload["picture"] as String?,
        )

        return userRepository.save(user)
    }
}