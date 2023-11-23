package pl.jutupe.cartogobackend.order.domain.service

import org.springframework.stereotype.Service
import pl.jutupe.cartogobackend.order.application.model.OrderRequest
import pl.jutupe.cartogobackend.order.domain.model.Order
import pl.jutupe.cartogobackend.order.infrastructure.OrderRepository
import pl.jutupe.cartogobackend.user.domain.model.User
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
}