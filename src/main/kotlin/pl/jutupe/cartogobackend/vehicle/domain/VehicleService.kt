package pl.jutupe.cartogobackend.vehicle.domain

import org.springframework.stereotype.Service
import pl.jutupe.cartogobackend.user.domain.model.User
import pl.jutupe.cartogobackend.vehicle.application.model.VehicleRequest
import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle
import pl.jutupe.cartogobackend.vehicle.infrastructure.VehicleRepository

@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
) {

    fun create(user: User, request: VehicleRequest): Vehicle {
        val vehicle = Vehicle(
            rentalId = user.rental?.id ?: throw IllegalStateException("User has no rental"),
            registrationNumber = request.registrationNumber,
            name = request.name,
            state = Vehicle.State(
                mileage = request.state.mileage,
                fuelLevel = request.state.fuelLevel,
                condition = request.state.condition,
            ),
        )

        return vehicleRepository.save(vehicle)
    }

    fun update(vehicle: Vehicle, request: VehicleRequest): Vehicle {
        return vehicleRepository.save(vehicle.copy(
            registrationNumber = request.registrationNumber,
            name = request.name,
            state = Vehicle.State(
                mileage = request.state.mileage,
                fuelLevel = request.state.fuelLevel,
                condition = request.state.condition,
            ),
        ))
    }
}