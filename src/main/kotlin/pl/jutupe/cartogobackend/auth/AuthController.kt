package pl.jutupe.cartogobackend.auth

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.security.PermitAll

@RestController
@RequestMapping("v1/auth")
class AuthController(
    private val googleService: GoogleService,
) {

    @PostMapping("google")
    fun google(
        @RequestBody(required = true) googleToken: String
    ): String {
        println(googleToken)

        val token = googleService.verify(googleToken)

        return token.payload.email
    }
}