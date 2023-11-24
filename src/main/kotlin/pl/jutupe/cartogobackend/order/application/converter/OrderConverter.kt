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
            vehicle = vehicleConverter.toResponse(order.vehicle),
            delivery = order.delivery?.let {
                OrderResponse.Delivery(
                    operatorId = it.operator.id,
                    address = it.address,
                    pesel = it.pesel,
                    nip = it.nip,
                    invoiceData = it.invoiceData,
                    drivingLicenseNumber = it.drivingLicenseNumber,
                    idNumber = it.idNumber,
                )
            },
            reception = order.reception?.let {
                OrderResponse.Reception(
                    operatorId = it.operator.id,
                    address = it.address,
                )
            },
            isDone = order.delivery != null && order.reception != null,
        )
    }
}