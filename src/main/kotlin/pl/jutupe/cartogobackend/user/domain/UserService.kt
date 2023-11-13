package pl.jutupe.cartogobackend.user.domain

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import org.springframework.stereotype.Service
import pl.jutupe.cartogobackend.storage.domain.model.FileResource
import pl.jutupe.cartogobackend.storage.domain.model.UserSignatureFileResource
import pl.jutupe.cartogobackend.storage.domain.model.VehicleImageFileResource
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
            signature = null,
            email = token.payload["email"] as String,
            avatar = token.payload["picture"] as String?,
            rental = null,
        )

        return userRepository.save(user)
    }

    fun getSignatureFileResource(user: User): FileResource? {
        val nameWithExtension = user.signature?.substringAfterLast('/')
            ?: return null

        return UserSignatureFileResource(
            userId = user.id,
            nameWithExtension = nameWithExtension,
        )
    }

    fun setSignature(user: User, signature: FileResource?): User {
        return userRepository.save(user.copy(
            signature = signature?.pathWithName,
        ))
    }
}