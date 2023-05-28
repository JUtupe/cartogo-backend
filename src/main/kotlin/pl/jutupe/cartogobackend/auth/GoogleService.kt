package pl.jutupe.cartogobackend.auth

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GoogleService(
    @Value("\${google.clientId}")
    private val googleClientId: String,
) {

    val verifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
        .setAudience(mutableListOf(googleClientId))
        .build()

    fun verify(token: String) = verifier.verify(token)
}