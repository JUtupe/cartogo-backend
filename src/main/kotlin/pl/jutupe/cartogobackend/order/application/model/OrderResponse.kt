package pl.jutupe.cartogobackend.order.application.model

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
) {

    data class Customer(
        val firstName: String,
        val lastName: String,
        val email: String,
        val phoneNumber: String,
    )
}