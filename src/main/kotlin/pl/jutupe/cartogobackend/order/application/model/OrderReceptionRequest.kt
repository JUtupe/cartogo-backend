package pl.jutupe.cartogobackend.order.application.model

import pl.jutupe.cartogobackend.common.Address

data class OrderReceptionRequest(
    val address: Address,
    val vehicleState: OrderVehicleStateRequest,
)