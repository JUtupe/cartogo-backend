package pl.jutupe.cartogobackend.order.application.model

import pl.jutupe.cartogobackend.common.Address

data class OrderDeliveryRequest(
    val address: Address,
    val pesel: String?,
    val nip: String?,
    val invoiceData: String,
    val drivingLicenseNumber: String,
    val idNumber: String,
    val vehicleState: OrderVehicleStateRequest,
)