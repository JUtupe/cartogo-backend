package pl.jutupe.cartogobackend.auth.application

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.jutupe.cartogobackend.auth.JwtTokenUtil
import pl.jutupe.cartogobackend.auth.domain.AuthService
import pl.jutupe.cartogobackend.user.application.converter.UserConverter

@RestController
@RequestMapping("v1/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenUtil: JwtTokenUtil,
    private val userConverter: UserConverter,
) {

    @PostMapping("google")
    fun google(
        @RequestBody(required = true) googleToken: String
    ): ResponseEntity<AuthResponse> {
        val principal = authService.principalByUserToken(googleToken)

        val token = jwtTokenUtil.generateAccessToken(principal)

        return ResponseEntity.ok(AuthResponse(
            accessToken = token,
            user = userConverter.convert(principal.user)
        ))
    }
}