package pl.jutupe.cartogobackend.auth.application

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.jutupe.cartogobackend.auth.application.model.AuthResponse
import pl.jutupe.cartogobackend.auth.domain.AuthService
import pl.jutupe.cartogobackend.auth.domain.UserPrincipal
import pl.jutupe.cartogobackend.rental.application.converter.RentalConverter
import pl.jutupe.cartogobackend.rental.application.model.RentalInvitationResponse
import pl.jutupe.cartogobackend.rental.domain.model.Rental
import pl.jutupe.cartogobackend.rental.domain.model.RentalInvitation
import pl.jutupe.cartogobackend.rental.infrastructure.RentalInviteRepository
import pl.jutupe.cartogobackend.rental.infrastructure.RentalRepository
import pl.jutupe.cartogobackend.user.application.converter.UserConverter
import pl.jutupe.cartogobackend.user.domain.model.User
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("v1/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenUtil: JwtTokenUtil,
    private val userConverter: UserConverter,
    private val rentalConverter: RentalConverter,
    private val rentalRepository: RentalRepository,
    private val inviteRepository: RentalInviteRepository,
) {

    @PostMapping("google")
    fun google(
        @RequestBody(required = true) googleToken: String
    ): ResponseEntity<AuthResponse> {
        val principal = authService.principalByUserToken(googleToken)

        val user = principal.user
        val token = jwtTokenUtil.generateAccessToken(user)

        return ResponseEntity.ok(
            buildAuthResponse(
                accessToken = token,
                user = principal.user,
            )
        )
    }

    @GetMapping("@me")
    fun getMe(
        @AuthenticationPrincipal principal: UserPrincipal?,
    ): ResponseEntity<AuthResponse> {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val user = principal.user
        val token = jwtTokenUtil.generateAccessToken(user)

        return ResponseEntity.ok(
            buildAuthResponse(
                accessToken = token,
                user = principal.user,
            )
        )
    }

    private fun buildAuthResponse(
        accessToken: String,
        user: User,
    ): AuthResponse {
        val pendingInvitation = inviteRepository.findByEmail(user.email)
        val invitationRental = pendingInvitation?.let { invite ->
            rentalRepository.findById(invite.rentalId).getOrNull()
        }

        return AuthResponse(
            accessToken = accessToken,
            user = userConverter.toResponse(user),
            rental = user.rental?.let { rentalConverter.toResponse(it) },
            properties = AuthResponse.Properties(
                isMemberOfAnyRental = user.rental != null,
                isRentalOwner = user.rental?.ownerId == user.id,
                pendingInvitation = pendingInvitation?.let { invitation ->
                    RentalInvitationResponse(
                        id = invitation.id,
                        rentalId = invitation.rentalId,
                        rentalName = invitationRental?.name ?: "Unknown rental",
                        email = invitation.email,
                    )
                },
            )
        )
    }

}