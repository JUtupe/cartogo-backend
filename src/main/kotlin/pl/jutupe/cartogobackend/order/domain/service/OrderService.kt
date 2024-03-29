package pl.jutupe.cartogobackend.order.domain.service

import org.springframework.stereotype.Service
import pl.jutupe.cartogobackend.order.application.model.OrderDeliveryRequest
import pl.jutupe.cartogobackend.order.application.model.OrderReceptionRequest
import pl.jutupe.cartogobackend.order.application.model.OrderRequest
import pl.jutupe.cartogobackend.order.domain.model.Order
import pl.jutupe.cartogobackend.order.domain.model.OrderDelivery
import pl.jutupe.cartogobackend.order.domain.model.OrderReception
import pl.jutupe.cartogobackend.order.infrastructure.OrderRepository
import pl.jutupe.cartogobackend.storage.domain.model.DeliveryCustomerSignatureFileResource
import pl.jutupe.cartogobackend.storage.domain.model.FileResource
import pl.jutupe.cartogobackend.storage.domain.model.ReceptionCustomerSignatureFileResource
import pl.jutupe.cartogobackend.storage.domain.model.UserSignatureFileResource
import pl.jutupe.cartogobackend.user.domain.model.User
import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle
import pl.jutupe.cartogobackend.vehicle.infrastructure.VehicleRepository

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val vehicleRepository: VehicleRepository,
) {
    fun create(request: OrderRequest, user: User): Order {
        val order = Order(
            number = request.number,
            amount = request.amount,
            paymentMethod = request.paymentMethod,
            deliveryDate = request.deliveryDate,
            receptionDate = request.receptionDate,
            customer = request.customer,
            vehicle = vehicleRepository.findById(request.vehicleId).orElseThrow(),
            rental = user.rental!!,
        )

        return orderRepository.save(order)
    }

    fun delete(order: Order) {
        orderRepository.delete(order)
    }

    fun update(request: OrderRequest, order: Order, user: User): Order {
        return orderRepository.save(order.copy(
            number = request.number,
            amount = request.amount,
            paymentMethod = request.paymentMethod,
            deliveryDate = request.deliveryDate,
            receptionDate = request.receptionDate,
            customer = request.customer,
            vehicle = vehicleRepository.findById(request.vehicleId).orElseThrow(),
        ))
    }

    fun createDelivery(request: OrderDeliveryRequest, signaturePath: String, order: Order, user: User): Order {
        return orderRepository.save(order.copy(
            delivery = OrderDelivery(
                orderId = order.id,
                operator = user,
                address = request.address,
                customerSignature = signaturePath,
                pesel = request.pesel,
                nip = request.nip,
                invoiceData = request.invoiceData,
                drivingLicenseNumber = request.drivingLicenseNumber,
                idNumber = request.idNumber,
            )
        )).also {
            vehicleRepository.save(it.vehicle.copy(
                state = it.vehicle.state.copy(
                    mileage = request.vehicleState.mileage,
                    fuelLevel = request.vehicleState.fuelLevel,
                    condition = request.vehicleState.condition,
                    location = Vehicle.State.Location.CUSTOMER,
                )
            ))
        }
    }

    fun createReception(request: OrderReceptionRequest, signaturePath: String, order: Order, user: User): Order {
        return orderRepository.save(order.copy(
            reception = OrderReception(
                orderId = order.id,
                operator = user,
                address = request.address,
                customerSignature = signaturePath,
            )
        )).also {
            vehicleRepository.save(it.vehicle.copy(
                state = it.vehicle.state.copy(
                    mileage = request.vehicleState.mileage,
                    fuelLevel = request.vehicleState.fuelLevel,
                    condition = request.vehicleState.condition,
                    location = Vehicle.State.Location.RENTAL,
                )
            ))
        }
    }

    fun getDeliveryCustomerSignatureFileResource(order: Order): FileResource? {
        val nameWithExtension = order.delivery?.customerSignature?.substringAfterLast('/')
            ?: return null

        return DeliveryCustomerSignatureFileResource(
            rentalId = order.rental.id,
            orderId = order.id,
            nameWithExtension = nameWithExtension,
        )
    }

    fun getReceptionCustomerSignatureFileResource(order: Order): FileResource? {
        val nameWithExtension = order.reception?.customerSignature?.substringAfterLast('/')
            ?: return null

        return ReceptionCustomerSignatureFileResource(
            rentalId = order.rental.id,
            orderId = order.id,
            nameWithExtension = nameWithExtension,
        )
    }
}