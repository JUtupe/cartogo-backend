package pl.jutupe.cartogobackend.vehicle.application.converter

import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.storage.application.converter.FileUrlConverter
import pl.jutupe.cartogobackend.vehicle.application.model.VehicleResponse
import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle

@Component
class VehicleConverter(
    private val fileUrlConverter: FileUrlConverter
) {

    fun toResponse(vehicle: Vehicle): VehicleResponse =
        VehicleResponse(
            id = vehicle.id,
            rentalId = vehicle.rentalId,
            registrationNumber = vehicle.registrationNumber,
            name = vehicle.name,
            image = vehicle.image?.let { fileUrlConverter.convert(it).url },
            state = VehicleResponse.State(
                mileage = vehicle.state.mileage,
                fuelLevel = vehicle.state.fuelLevel,
                condition = vehicle.state.condition,
            ),
        )
}