package pl.jutupe.cartogobackend.order.application

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.jutupe.cartogobackend.auth.domain.UserPrincipal
import pl.jutupe.cartogobackend.common.extension
import pl.jutupe.cartogobackend.order.application.converter.OrderConverter
import pl.jutupe.cartogobackend.order.application.model.OrderDeliveryRequest
import pl.jutupe.cartogobackend.order.application.model.OrderReceptionRequest
import pl.jutupe.cartogobackend.order.application.model.OrderRequest
import pl.jutupe.cartogobackend.order.application.model.OrderResponse
import pl.jutupe.cartogobackend.order.domain.exception.OrderNotFoundException
import pl.jutupe.cartogobackend.order.domain.service.OrderService
import pl.jutupe.cartogobackend.order.infrastructure.FormGenerator
import pl.jutupe.cartogobackend.order.infrastructure.OrderRepository
import pl.jutupe.cartogobackend.rental.domain.exceptions.RentalNotFoundException
import pl.jutupe.cartogobackend.storage.domain.StorageService
import pl.jutupe.cartogobackend.storage.domain.model.DeliveryCustomerSignatureFileResource
import pl.jutupe.cartogobackend.storage.domain.model.ReceptionCustomerSignatureFileResource
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@RestController
@RequestMapping("v1/orders")
class OrderController(
    private val orderService: OrderService,
    private val orderConverter: OrderConverter,
    private val orderRepository: OrderRepository,
    private val storageService: StorageService,
    private val formGenerator: FormGenerator,
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

    @PostMapping("{id}/delivery", consumes = ["multipart/form-data", "application/json"])
    fun createDelivery(
        @PathVariable id: String,
        @RequestPart("signature", required = true) signature: MultipartFile,
        @RequestPart("form") formMultipart: String,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): OrderResponse {
        val request = jacksonObjectMapper().readValue(formMultipart, OrderDeliveryRequest::class.java)
        principal.user.rental ?: throw RentalNotFoundException(rentalId = "@me")

        val order = orderRepository.findByIdOrNull(id)
            ?: throw OrderNotFoundException(orderId = id)

        if (order.rental.id != principal.user.rental.id) {
            throw OrderNotFoundException(orderId = id)
        }
        val signaturePath = DeliveryCustomerSignatureFileResource(
            rentalId = order.rental.id,
            orderId = order.id,
            nameWithExtension = (UUID.randomUUID().toString() + '.' + signature.extension),
        ).let { storageService.saveImage(signature, it) }


        val updatedOrder = orderService.createDelivery(request, signaturePath.pathWithName, order, principal.user)

        val form = formGenerator.createDeliveryForm(updatedOrder)

        //save file to storage
        val formPath = Files.createFile(Path.of("form.pdf"))
        Files.write(formPath, form.byteArray)


        return orderConverter.toResponse(updatedOrder)
    }

    @PostMapping("{id}/reception", consumes = ["multipart/form-data", "application/json"])
    fun createReception(
        @PathVariable id: String,
        @RequestPart("signature", required = true) signature: MultipartFile,
        @RequestPart("form") formMultipart: String,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): OrderResponse {
        val request = jacksonObjectMapper().readValue(formMultipart, OrderReceptionRequest::class.java)

        principal.user.rental ?: throw RentalNotFoundException(rentalId = "@me")

        val order = orderRepository.findByIdOrNull(id)
            ?: throw OrderNotFoundException(orderId = id)

        if (order.rental.id != principal.user.rental.id) {
            throw OrderNotFoundException(orderId = id)
        }
        val signaturePath = ReceptionCustomerSignatureFileResource(
            rentalId = order.rental.id,
            orderId = order.id,
            nameWithExtension = (UUID.randomUUID().toString() + '.' + signature.extension),
        ).let { storageService.saveImage(signature, it) }


        val updatedOrder = orderService.createReception(request, signaturePath.pathWithName, order, principal.user)


        val form = formGenerator.createReceptionForm(updatedOrder)

        //save file to storage
        val formPath = Files.createFile(Path.of("form.pdf"))
        Files.write(formPath, form.byteArray)

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