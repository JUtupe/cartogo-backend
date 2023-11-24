package pl.jutupe.cartogobackend.order.application

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import pl.jutupe.cartogobackend.auth.domain.UserPrincipal
import pl.jutupe.cartogobackend.order.application.converter.OrderConverter
import pl.jutupe.cartogobackend.order.application.model.OrderDeliveryRequest
import pl.jutupe.cartogobackend.order.application.model.OrderReceptionRequest
import pl.jutupe.cartogobackend.order.application.model.OrderRequest
import pl.jutupe.cartogobackend.order.application.model.OrderResponse
import pl.jutupe.cartogobackend.order.domain.exception.OrderNotFoundException
import pl.jutupe.cartogobackend.order.domain.service.OrderService
import pl.jutupe.cartogobackend.order.infrastructure.OrderRepository
import pl.jutupe.cartogobackend.rental.domain.exceptions.RentalNotFoundException

@RestController
@RequestMapping("v1/orders")
class OrderController(
    private val orderService: OrderService,
    private val orderConverter: OrderConverter,
    private val orderRepository: OrderRepository,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(
        @RequestBody request: OrderRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): OrderResponse {
        principal.user.rental ?: throw RentalNotFoundException(rentalId = "@me")

        val order = orderService.create(request, principal.user)

        return orderConverter.toResponse(order)
    }

    @PutMapping("{id}")
    fun editOrder(
        @PathVariable id: String,
        @RequestBody request: OrderRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): OrderResponse {
        principal.user.rental ?: throw RentalNotFoundException(rentalId = "@me")

        val order = orderRepository.findByIdOrNull(id)
            ?: throw OrderNotFoundException(orderId = id)

        if (order.rental.id != principal.user.rental.id) {
            throw OrderNotFoundException(orderId = id)
        }

        val updatedOrder = orderService.update(request, order, principal.user)

        return orderConverter.toResponse(updatedOrder)
    }

    @PostMapping("{id}/delivery")
    fun createDelivery(
        @PathVariable id: String,
        @RequestBody request: OrderDeliveryRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): OrderResponse {
        principal.user.rental ?: throw RentalNotFoundException(rentalId = "@me")

        val order = orderRepository.findByIdOrNull(id)
            ?: throw OrderNotFoundException(orderId = id)

        if (order.rental.id != principal.user.rental.id) {
            throw OrderNotFoundException(orderId = id)
        }

        val updatedOrder = orderService.createDelivery(request, order, principal.user)

        return orderConverter.toResponse(updatedOrder)
    }

    @PostMapping("{id}/reception")
    fun createReception(
        @PathVariable id: String,
        @RequestBody request: OrderReceptionRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): OrderResponse {
        principal.user.rental ?: throw RentalNotFoundException(rentalId = "@me")

        val order = orderRepository.findByIdOrNull(id)
            ?: throw OrderNotFoundException(orderId = id)

        if (order.rental.id != principal.user.rental.id) {
            throw OrderNotFoundException(orderId = id)
        }

        val updatedOrder = orderService.createReception(request, order, principal.user)

        return orderConverter.toResponse(updatedOrder)
    }

    @GetMapping
    fun getOrders(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): List<OrderResponse> {
        val rental = principal.user.rental ?: throw RentalNotFoundException(rentalId = "@me")

        return orderRepository.findAllByRentalIdOrderByCreatedDate(rental.id).map { orderConverter.toResponse(it) }
    }

    @DeleteMapping("{id}")
    fun deleteOrder(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: UserPrincipal,
    ) {
        val rental = principal.user.rental ?: throw RentalNotFoundException(rentalId = "@me")

        val order = orderRepository.findByIdOrNull(id)
            ?: throw OrderNotFoundException(orderId = id)

        if (order.rental.id != rental.id) {
            throw OrderNotFoundException(orderId = id)
        }

        orderService.delete(order)
    }
}