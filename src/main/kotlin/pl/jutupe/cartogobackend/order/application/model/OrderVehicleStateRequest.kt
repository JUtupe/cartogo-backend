package pl.jutupe.cartogobackend.order.application.model

import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle

data class OrderVehicleStateRequest(
    val fuelLevel: Int,
    val mileage: Long,
    val condition: Vehicle.State.Condition,
)
