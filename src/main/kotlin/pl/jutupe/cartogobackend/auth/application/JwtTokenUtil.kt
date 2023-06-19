package pl.jutupe.cartogobackend.auth.application

import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.rental.domain.model.Rental
import pl.jutupe.cartogobackend.user.domain.model.User
import java.util.*

@Component
class JwtTokenUtil(
    @Value("\${jwt.secret}")
    private val secret: String,
) {

    fun generateAccessToken(user: User): String {
        return Jwts.builder()
            .setSubject(user.id)
            .setIssuer("Wypozyczajka")
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + EXPIRE_DURATION))
            .addClaims(mapOf(
                "user" to mapOf(
                    "id" to user.id,
                    "name" to user.name,
                    "email" to user.email,
                ),
                "rental" to user.rental?.let { mapOf(
                    "id" to it.id,
                    "name" to it.name,
                    "isOwner" to (it.ownerId == user.id),
                )},
            ))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    fun validateAccessToken(token: String?): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token)
            return true
        } catch (e: Exception) {
        }

        return false
    }

    fun getSubject(token: String): String? {
        return parseClaims(token).subject
    }

    private fun parseClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secret)
            .build()
            .parseClaimsJws(token)
            .body
    }

    companion object {
        private const val EXPIRE_DURATION = 1000 * 60 * 60 * 24 * 7 // 7 days
    }
}