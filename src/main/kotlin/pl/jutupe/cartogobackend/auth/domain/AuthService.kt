package pl.jutupe.cartogobackend.auth.domain

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.user.domain.UserService
import pl.jutupe.cartogobackend.user.domain.model.User
import pl.jutupe.cartogobackend.user.infrastructure.UserRepository

@Component
class AuthService(
    @Value("\${google.clientId}")
    private val googleClientId: String,
    private val userRepository: UserRepository,
    private val userService: UserService,
) {

    private val verifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
        .setAudience(mutableListOf(googleClientId))
        .build()

    fun principalByUserToken(googleToken: String): UserPrincipal {
        val token = verifier.verify(googleToken)

        val user = userRepository.findByGoogleId(token.payload.subject)
            ?: userService.create(token)

        return UserPrincipal(user)
    }
}