package pl.jutupe.cartogobackend.rental.application.model

import pl.jutupe.cartogobackend.user.application.model.UserResponse

data class RentalResponse(
    val id: String,
    val name: String,
    val nip: String,
    val address: Address,
    val owner: Owner,
    val ownerId: String,
    val users: Set<UserResponse>,
    val invitations: Set<RentalInvitationResponse>,
) {
    data class Address(
        val postalCode: String,
        val street: String,
        val city: String,
    )

    data class Owner(
        val firstName: String,
        val lastName: String,
    )
}