package pl.jutupe.cartogobackend.auth.application

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.jutupe.cartogobackend.auth.application.model.AuthResponse
import pl.jutupe.cartogobackend.auth.domain.AuthService
import pl.jutupe.cartogobackend.rental.application.converter.RentalConverter
import pl.jutupe.cartogobackend.rental.application.model.RentalInvitationResponse
import pl.jutupe.cartogobackend.rental.infrastructure.RentalInviteRepository
import pl.jutupe.cartogobackend.rental.infrastructure.RentalRepository
import pl.jutupe.cartogobackend.user.application.converter.UserConverter
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
        val rental = rentalRepository.findByUsersContaining(user).firstOrNull()

        val pendingInvitation = inviteRepository.findByEmail(user.email)
        val invitationRental = pendingInvitation?.let { invite ->
            rentalRepository.findById(invite.rentalId).getOrNull()
        }

        val token = jwtTokenUtil.generateAccessToken(user, rental)

        return ResponseEntity.ok(
            AuthResponse(
                accessToken = token,
                user = userConverter.toResponse(principal.user),
                rental = rental?.let { rentalConverter.toResponse(it) },
                properties = AuthResponse.Properties(
                    isMemberOfAnyRental = rental != null,
                    isRentalOwner = rental?.ownerId == user.id,
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
        )
    }
}