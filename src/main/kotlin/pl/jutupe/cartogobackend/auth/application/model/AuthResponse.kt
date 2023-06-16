package pl.jutupe.cartogobackend.auth.application.model

import pl.jutupe.cartogobackend.rental.application.model.RentalInvitationResponse
import pl.jutupe.cartogobackend.rental.application.model.RentalResponse
import pl.jutupe.cartogobackend.user.application.model.UserResponse

data class AuthResponse(
    val accessToken: String,
    val user: UserResponse,
    val rental: RentalResponse?,
    val properties: Properties,
) {

    data class Properties(
        val isMemberOfAnyRental: Boolean,
        val isRentalOwner: Boolean,
        val pendingInvitation: RentalInvitationResponse?,
    )
}