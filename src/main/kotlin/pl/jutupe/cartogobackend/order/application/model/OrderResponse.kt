package pl.jutupe.cartogobackend.order.application.model

import pl.jutupe.cartogobackend.common.Address
import pl.jutupe.cartogobackend.order.domain.model.Order
import pl.jutupe.cartogobackend.vehicle.application.model.VehicleResponse
import java.util.*

data class OrderResponse(
    val id: String,
    val number: String,
    val amount: Double,
    val paymentMethod: Order.PaymentMethod,
    val deliveryDate: Date,
    val receptionDate: Date,
    val customer: Customer,
    val vehicle: VehicleResponse,
    val delivery: Delivery?,
    val reception: Reception?,
    val isDone: Boolean,
) {

    data class Customer(
        val firstName: String,
        val lastName: String,
        val email: String,
        val phoneNumber: String,
    )

    data class Delivery(
        val operatorId: String,
        val address: Address,
        val pesel: String?,
        val nip: String?,
        val invoiceData: String,
        val drivingLicenseNumber: String,
        val idNumber: String,
    )

    data class Reception(
        val operatorId: String,
        val address: Address,
    )
}