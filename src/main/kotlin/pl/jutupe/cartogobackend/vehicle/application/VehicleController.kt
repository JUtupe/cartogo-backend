package pl.jutupe.cartogobackend.vehicle.application

import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import pl.jutupe.cartogobackend.auth.domain.UserPrincipal
import pl.jutupe.cartogobackend.common.exceptions.ForbiddenException
import pl.jutupe.cartogobackend.common.exceptions.NotFoundException
import pl.jutupe.cartogobackend.rental.domain.exceptions.RentalNotFoundException
import pl.jutupe.cartogobackend.vehicle.application.converter.VehicleConverter
import pl.jutupe.cartogobackend.vehicle.application.model.VehicleRequest
import pl.jutupe.cartogobackend.vehicle.application.model.VehicleResponse
import pl.jutupe.cartogobackend.vehicle.domain.VehicleService
import pl.jutupe.cartogobackend.vehicle.infrastructure.VehicleRepository

@RestController
@RequestMapping("v1/vehicles")
class VehicleController(
    private val vehicleService: VehicleService,
    private val vehicleConverter: VehicleConverter,
    private val vehicleRepository: VehicleRepository,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createVehicle(
        @RequestBody request: VehicleRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): VehicleResponse {
        val vehicle = vehicleService.create(principal.user, request)

        return vehicleConverter.toResponse(vehicle)
    }

    @PutMapping("/{id}")
    fun updateVehicle(
        @PathVariable id: String,
        @RequestBody request: VehicleRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): VehicleResponse {
        val vehicle = vehicleRepository.findById(id)
            .orElseThrow { NotFoundException("Vehicle with id $id not found") }

        if (vehicle.rentalId != principal.user.rental?.id) {
            throw ForbiddenException("Vehicle with id $id is not in your rental")
        }

        val updatedVehicle = vehicleService.update(vehicle, request)

        return vehicleConverter.toResponse(updatedVehicle)
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

        vehicleRepository.delete(vehicle)
    }
}