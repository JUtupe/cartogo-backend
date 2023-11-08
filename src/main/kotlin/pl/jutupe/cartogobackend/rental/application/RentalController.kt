package pl.jutupe.cartogobackend.rental.application

import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import pl.jutupe.cartogobackend.auth.domain.UserPrincipal
import pl.jutupe.cartogobackend.common.exceptions.BadRequestException
import pl.jutupe.cartogobackend.common.exceptions.ForbiddenException
import pl.jutupe.cartogobackend.rental.application.converter.RentalConverter
import pl.jutupe.cartogobackend.rental.application.model.RentalRequest
import pl.jutupe.cartogobackend.rental.application.model.RentalResponse
import pl.jutupe.cartogobackend.rental.domain.RentalService
import pl.jutupe.cartogobackend.rental.domain.exceptions.RentalInvitationNotFoundException
import pl.jutupe.cartogobackend.rental.domain.exceptions.RentalNotFoundException
import pl.jutupe.cartogobackend.rental.domain.exceptions.UserAlreadyInRentalException
import pl.jutupe.cartogobackend.rental.infrastructure.RentalInviteRepository
import pl.jutupe.cartogobackend.rental.infrastructure.RentalRepository
import pl.jutupe.cartogobackend.user.infrastructure.UserRepository
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("v1/rentals")
class RentalController(
    private val userRepository: UserRepository,
    private val rentalService: RentalService,
    private val rentalConverter: RentalConverter,
    private val rentalRepository: RentalRepository,
    private val inviteRepository: RentalInviteRepository,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createRental(
        @RequestBody request: RentalRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): RentalResponse {
        if (principal.user.rental != null) {
            throw UserAlreadyInRentalException(principal.user.id)
        }

        val rental = rentalService.create(request, principal.user)

        return rentalConverter.toResponse(rental)
    }

    @PutMapping("@me")
    fun updateRental(
        @RequestBody request: RentalRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): RentalResponse {
        val rental = principal.user.rental ?: throw RentalNotFoundException(rentalId = "@me")

        if (rental.ownerId != principal.user.id) {
            throw ForbiddenException("Only owner can edit rental")
        }

        val updatedRental = rentalService.update(rental, request, principal.user)

        return rentalConverter.toResponse(updatedRental)
    }

    @GetMapping("@me")
    fun getMyRental(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): RentalResponse {
        val rental = principal.user.rental ?: throw RentalNotFoundException()

        return rentalConverter.toResponse(rental)
    }

    @DeleteMapping("@me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMyRental(
        @AuthenticationPrincipal principal: UserPrincipal,
    ) {
        val rental = principal.user.rental ?: throw RentalNotFoundException()

        rentalService.delete(rental)
    }

    @PostMapping("invitations")
    fun inviteUser(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody email: String,
    ): RentalResponse {
        val rental = principal.user.rental ?: throw RentalNotFoundException()

        if (rental.ownerId != principal.user.id) {
            throw ForbiddenException("Only owner can invite users")
        }

        val updatedRental = rentalService.inviteEmail(rental, email)

        return rentalConverter.toResponse(updatedRental)
    }

    @PostMapping("invitations/{id}/accept")
    fun acceptInvitation(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: String,
    ) {
        val invitation = inviteRepository.findById(id).getOrNull()
            ?: throw RentalInvitationNotFoundException(id)

        if (principal.user.email != invitation.email) {
            throw ForbiddenException("Only invited user can accept invitation")
        }
        val rental = rentalRepository.findById(invitation.rentalId).getOrNull()
            ?: throw RentalNotFoundException(invitation.rentalId)

        rentalService.acceptInvitation(rental, invitation, principal.user)
    }

    @DeleteMapping("invitations/{id}")
    fun deleteInvitation(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: String,
    ) {
        val invitation = inviteRepository.findById(id).getOrNull()
            ?: throw RentalInvitationNotFoundException(id)

        val rental = rentalRepository.findById(invitation.rentalId).getOrNull()
            ?: throw RentalNotFoundException(invitation.rentalId)

        if (rental.ownerId != principal.user.id && principal.user.email != invitation.email) {
            throw ForbiddenException("Only owner or invited user can delete invitations")
        }

        rentalService.cancelInvitation(invitation)
    }

    @DeleteMapping("employees/{id}")
    fun deleteEmployee(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: String,
    ) {
        val employee = userRepository.findById(id).orElseThrow {
            BadRequestException("User with id=$id not found")
        }
        val rental = employee.rental ?: throw BadRequestException("User is not in rental")

        if (rental.ownerId != principal.user.id) {
            throw ForbiddenException("Only owner can delete employees")
        }

        if (id == rental.ownerId) {
            throw BadRequestException("Owner cannot delete himself")
        }

        rentalService.removeEmployee(rental, employee)
    }
}