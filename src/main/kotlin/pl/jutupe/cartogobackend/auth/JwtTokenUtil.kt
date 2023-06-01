package pl.jutupe.cartogobackend.auth

import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.auth.domain.UserPrincipal
import java.util.*


@Component
class JwtTokenUtil(
    @Value("\${jwt.secret}")
    private val secret: String,
) {

    fun generateAccessToken(principal: UserPrincipal): String {
        return Jwts.builder()
            .setSubject(principal.username)
            .setIssuer("Wypozyczajka")
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + EXPIRE_DURATION))
            .addClaims(mapOf(
                "user" to mapOf(
                    "id" to principal.user.id,
                    "name" to principal.user.name,
                    "email" to principal.user.email,
                )
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