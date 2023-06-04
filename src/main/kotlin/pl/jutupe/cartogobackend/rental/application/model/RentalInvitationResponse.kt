package pl.jutupe.cartogobackend.rental.application.model

data class RentalInvitationResponse(
    val id: String,
    val rentalId: String,
    val rentalName: String,
    val email: String,
)