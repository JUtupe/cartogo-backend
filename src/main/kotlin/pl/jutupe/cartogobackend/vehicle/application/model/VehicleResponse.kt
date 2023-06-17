package pl.jutupe.cartogobackend.vehicle.application.model

import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle

data class VehicleResponse(
    val id: String,
    val rentalId: String,
    val registrationNumber: String,
    val name: String,
    val image: String?,
    val state: State,
) {
    data class State(
        val mileage: Long,
        val fuelLevel: Int,
        val condition: Vehicle.State.Condition
    )
}