package pl.jutupe.cartogobackend.rental.application.converter

import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.rental.application.model.RentalInvitationResponse
import pl.jutupe.cartogobackend.rental.application.model.RentalResponse
import pl.jutupe.cartogobackend.rental.domain.model.Rental
import pl.jutupe.cartogobackend.user.application.converter.UserConverter
import pl.jutupe.cartogobackend.user.infrastructure.UserRepository

@Component
class RentalConverter(
    private val userConverter: UserConverter,
    private val userRepository: UserRepository,
) {

    fun toResponse(rental: Rental): RentalResponse {
        val users = userRepository.findByRentalId(rental.id)

        return RentalResponse(
            id = rental.id,
            name = rental.name,
            nip = rental.nip,
            address = RentalResponse.Address(
                postalCode = rental.address.postalCode,
                street = rental.address.street,
                city = rental.address.city,
            ),
            owner = RentalResponse.Owner(
                firstName = rental.owner.firstName,
                lastName = rental.owner.lastName,
            ),
            ownerId = rental.ownerId,
            users = users.map {
                userConverter.toResponse(it)
            }.toSet(),
            invitations = rental.invites.map {
                RentalInvitationResponse(
                    id = it.id,
                    rentalId = it.rentalId,
                    rentalName = rental.name,
                    email = it.email,
                )
            }.toSet()
        )
    }
}