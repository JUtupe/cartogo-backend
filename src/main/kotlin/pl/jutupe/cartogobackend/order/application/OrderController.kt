package pl.jutupe.cartogobackend.order.application

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
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
import pl.jutupe.cartogobackend.order.domain.model.Order
import pl.jutupe.cartogobackend.order.domain.service.OrderService
import pl.jutupe.cartogobackend.order.infrastructure.FormGenerator
import pl.jutupe.cartogobackend.order.infrastructure.OrderRepository
import pl.jutupe.cartogobackend.rental.domain.exceptions.RentalNotFoundException
import pl.jutupe.cartogobackend.storage.domain.StorageService
import pl.jutupe.cartogobackend.storage.domain.model.DeliveryCustomerSignatureFileResource
import pl.jutupe.cartogobackend.storage.domain.model.ReceptionCustomerSignatureFileResource
import java.util.*

@RestController
@RequestMapping("v1/orders")
class OrderController(
    private val orderService: OrderService,
    private val orderConverter: OrderConverter,
    private val orderRepository: OrderRepository,
    private val storageService: StorageService,
    private val formGenerator: FormGenerator,
    private val emailSender: JavaMailSender,
    @Value("\${spring.mail.username:}")
    private val mailFrom: String,
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

        runCatching {
            sendDeliveryMail(updatedOrder)
        }.onFailure {
            it.printStackTrace()
        }

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

        runCatching {
            sendReceptionMail(updatedOrder)
        }.onFailure {
            it.printStackTrace()
        }

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


    fun sendDeliveryMail(order: Order) {
        val form = formGenerator.createDeliveryForm(order)

        val mail = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(mail, true)

        helper.setTo(order.customer.email)
        helper.setFrom(mailFrom)
        helper.setSubject("${order.rental.name} - Protokół wydania pojazdu")
        helper.setText("W załączniku znajduje się protokół wydania pojazdu.")
        helper.addAttachment("Protokół wydania pojazdu.pdf", form)

        emailSender.send(mail)
    }

    fun sendReceptionMail(order: Order) {
        val form = formGenerator.createReceptionForm(order)

        val mail = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(mail, true)

        helper.setTo(order.customer.email)
        helper.setFrom(mailFrom)
        helper.setSubject("${order.rental.name} - Protokół odbioru pojazdu")
        helper.setText("W załączniku znajduje się protokół odbioru pojazdu.")
        helper.addAttachment("Protokół odbioru pojazdu.pdf", form)

        emailSender.send(mail)
    }
}