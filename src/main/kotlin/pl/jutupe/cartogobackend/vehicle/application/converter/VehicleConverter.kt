package pl.jutupe.cartogobackend.vehicle.application.converter

import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.vehicle.application.model.VehicleResponse
import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle

@Component
class VehicleConverter {

    fun toResponse(vehicle: Vehicle): VehicleResponse =
        VehicleResponse(
            id = vehicle.id,
            rentalId = vehicle.rentalId,
            registrationNumber = vehicle.registrationNumber,
            name = vehicle.name,
            state = VehicleResponse.State(
                mileage = vehicle.state.mileage,
                fuelLevel = vehicle.state.fuelLevel,
                condition = vehicle.state.condition,
            ),
        )
}