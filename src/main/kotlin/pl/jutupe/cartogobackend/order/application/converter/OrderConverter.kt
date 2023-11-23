package pl.jutupe.cartogobackend.order.application.converter

import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.order.application.model.OrderResponse
import pl.jutupe.cartogobackend.order.domain.model.Order
import pl.jutupe.cartogobackend.vehicle.application.converter.VehicleConverter

@Component
class OrderConverter(
    private val vehicleConverter: VehicleConverter,
) {
    fun toResponse(order: Order): OrderResponse {
        return OrderResponse(
            id = order.id,
            number = order.number,
            amount = order.amount,
            paymentMethod = order.paymentMethod,
            deliveryDate = order.deliveryDate,
            receptionDate = order.receptionDate,
            customer = OrderResponse.Customer(
                firstName = order.customer.firstName,
                lastName = order.customer.lastName,
                email = order.customer.email,
                phoneNumber = order.customer.phoneNumber,
            ),
            vehicle = vehicleConverter.toResponse(order.vehicle)
        )
    }
}