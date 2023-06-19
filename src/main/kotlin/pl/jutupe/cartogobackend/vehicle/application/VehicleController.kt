package pl.jutupe.cartogobackend.vehicle.application

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.jutupe.cartogobackend.auth.domain.UserPrincipal
import pl.jutupe.cartogobackend.common.exceptions.BadRequestException
import pl.jutupe.cartogobackend.common.exceptions.ForbiddenException
import pl.jutupe.cartogobackend.common.exceptions.NotFoundException
import pl.jutupe.cartogobackend.common.extension
import pl.jutupe.cartogobackend.rental.domain.exceptions.RentalNotFoundException
import pl.jutupe.cartogobackend.storage.domain.StorageService
import pl.jutupe.cartogobackend.storage.domain.model.VehicleImageFileResource
import pl.jutupe.cartogobackend.vehicle.application.converter.VehicleConverter
import pl.jutupe.cartogobackend.vehicle.application.model.VehicleRequest
import pl.jutupe.cartogobackend.vehicle.application.model.VehicleResponse
import pl.jutupe.cartogobackend.vehicle.domain.VehicleService
import pl.jutupe.cartogobackend.vehicle.infrastructure.VehicleRepository
import java.util.UUID

@RestController
@RequestMapping("v1/vehicles")
class VehicleController(
    private val vehicleService: VehicleService,
    private val vehicleConverter: VehicleConverter,
    private val vehicleRepository: VehicleRepository,
    private val storageService: StorageService,
) {

    @PostMapping(consumes = ["multipart/form-data", "application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    fun createVehicle(
        @RequestPart("image", required = false) image: MultipartFile?,
        @RequestPart("form") formMultipart: String,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): VehicleResponse {
        val request = jacksonObjectMapper().readValue(formMultipart, VehicleRequest::class.java)

        if (principal.user.rental == null) {
            throw BadRequestException("No rental found for user ${principal.user.id}")
        }

        val vehicle = vehicleService.create(principal.user, request)

        val vehicleWithImage = image?.let {
            val resource = VehicleImageFileResource(
                rentalId = vehicle.rentalId,
                vehicleId = vehicle.id,
                nameWithExtension = (UUID.randomUUID().toString() + '.' + it.extension),
            )
            storageService.saveImage(it, resource)

            vehicleService.setImage(vehicle, resource)
        }

        return vehicleConverter.toResponse(vehicleWithImage ?: vehicle)
    }

    @PutMapping("/{id}", consumes = ["multipart/form-data", "application/json"])
    fun updateVehicle(
        @PathVariable id: String,
        @RequestPart("image", required = false) image: MultipartFile?,
        @RequestPart("form") formMultipart: String,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): VehicleResponse {
        val request = jacksonObjectMapper().readValue(formMultipart, VehicleRequest::class.java)

        if (principal.user.rental == null) {
            throw BadRequestException("No rental found for user ${principal.user.id}")
        }

        val vehicle = vehicleRepository.findById(id)
            .orElseThrow { NotFoundException("Vehicle with id $id not found") }

        if (vehicle.rentalId != principal.user.rental.id) {
            throw ForbiddenException("Vehicle with id $id is not in your rental")
        }

        val updatedVehicle = vehicleService.update(vehicle, request)

        val vehicleWithImage = image?.let {
            vehicleService.getCarImageFileResource(vehicle)
                ?.let { storageService.removeResource(it) }

            val resource = VehicleImageFileResource(
                rentalId = vehicle.rentalId,
                vehicleId = vehicle.id,
                nameWithExtension = it.originalFilename ?: throw BadRequestException("Image is required"),
            )
            storageService.saveImage(it, resource)

            vehicleService.setImage(vehicle, resource)
        }

        return vehicleConverter.toResponse(vehicleWithImage ?: updatedVehicle)
    }

    @GetMapping("/{id}")
    fun getVehicle(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): VehicleResponse {
        val vehicle = vehicleRepository.findById(id)
            .orElseThrow { NotFoundException("Vehicle with id $id not found") }

        if (vehicle.rentalId != principal.user.rental?.id) {
            throw ForbiddenException("Vehicle with id $id is not in your rental")
        }

        return vehicleConverter.toResponse(vehicle)
    }

    @GetMapping
    fun getVehicles(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): List<VehicleResponse> {
        val rental = principal.user.rental ?: throw RentalNotFoundException()

        val vehicles = vehicleRepository.findByRentalId(rental.id)

        return vehicles.map { vehicleConverter.toResponse(it) }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteVehicle(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: UserPrincipal,
    ) {
        val vehicle = vehicleRepository.findById(id)
            .orElseThrow { NotFoundException("Vehicle with id $id not found") }

        if (vehicle.rentalId != principal.user.rental?.id) {
            throw ForbiddenException("Vehicle with id $id is not in your rental")
        }

        vehicleService.delete(vehicle)
    }

    @DeleteMapping("/{id}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteVehicleImage(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: UserPrincipal,
    ) {
        val vehicle = vehicleRepository.findById(id)
            .orElseThrow { NotFoundException("Vehicle with id $id not found") }

        if (vehicle.rentalId != principal.user.rental?.id) {
            throw ForbiddenException("Vehicle with id $id is not in your rental")
        }

        vehicleService.getCarImageFileResource(vehicle)
            ?.let { storageService.removeResource(it) }

        vehicleService.setImage(vehicle, null)
    }
}