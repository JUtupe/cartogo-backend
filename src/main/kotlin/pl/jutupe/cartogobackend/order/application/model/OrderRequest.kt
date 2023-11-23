package pl.jutupe.cartogobackend.order.application.model

import pl.jutupe.cartogobackend.order.domain.model.Order
import java.util.Date

data class OrderRequest(
    val number: String,
    val vehicleId: String,
    val amount: Double,
    val paymentMethod: Order.PaymentMethod,
    val deliveryDate: Date,
    val receptionDate: Date,
    val customer: Order.Customer,
)