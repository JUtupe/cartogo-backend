package pl.jutupe.cartogobackend.rental.domain

import org.springframework.stereotype.Service
import pl.jutupe.cartogobackend.rental.application.model.RentalRequest
import pl.jutupe.cartogobackend.rental.domain.model.Rental
import pl.jutupe.cartogobackend.rental.domain.model.RentalInvitation
import pl.jutupe.cartogobackend.rental.infrastructure.RentalInviteRepository
import pl.jutupe.cartogobackend.rental.infrastructure.RentalRepository
import pl.jutupe.cartogobackend.storage.domain.StorageService
import pl.jutupe.cartogobackend.storage.domain.model.RentalDirectoryResource
import pl.jutupe.cartogobackend.user.domain.model.User
import pl.jutupe.cartogobackend.user.infrastructure.UserRepository

@Service
class RentalService(
    private val rentalRepository: RentalRepository,
    private val inviteRepository: RentalInviteRepository,
    private val userRepository: UserRepository,
    private val storageService: StorageService,
) {

    fun create(request: RentalRequest, user: User): Rental {
        val rental = Rental(
            name = request.name,
            nip = request.nip,
            address = Rental.Address(
                postalCode = request.address.postalCode,
                street = request.address.street,
                city = request.address.city,
            ),
            owner = Rental.Owner(
                firstName = request.owner.firstName,
                lastName = request.owner.lastName,
            ),
            ownerId = user.id,
            invites = mutableSetOf(),
        )

        val savedRental = rentalRepository.save(rental)
        userRepository.save(user.copy(rental = rental))

        return savedRental
    }

    fun inviteEmail(rental: Rental, email: String): Rental {
        val invite = inviteRepository.findByEmail(email)
            ?: inviteRepository.save(RentalInvitation(email = email, rentalId = rental.id))

        rental.invites.add(invite)

        return rentalRepository.save(rental)
    }

    fun acceptInvitation(rental: Rental, invitation: RentalInvitation, user: User): Rental {
        inviteRepository.delete(invitation)

        userRepository.save(user.copy(rental = rental))

        return rentalRepository.save(rental)
    }

    fun cancelInvitation(invite: RentalInvitation) {
        inviteRepository.delete(invite)
    }

    fun removeEmployee(rental: Rental, user: User): Rental {
        userRepository.save(user.copy(rental = null))

        return rentalRepository.save(rental)
    }

    fun delete(rental: Rental) {
        rentalRepository.delete(rental)

        storageService.removeDirectory(RentalDirectoryResource(rentalId = rental.id))
    }
}