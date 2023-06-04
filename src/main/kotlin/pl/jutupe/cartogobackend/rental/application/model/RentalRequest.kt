package pl.jutupe.cartogobackend.rental.application.model

data class RentalRequest(
    val name: String,
    val nip: String,
    val address: Address,
    val owner: Owner,
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