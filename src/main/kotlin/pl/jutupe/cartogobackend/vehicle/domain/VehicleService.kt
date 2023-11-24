package pl.jutupe.cartogobackend.vehicle.domain

import org.springframework.stereotype.Service
import pl.jutupe.cartogobackend.storage.domain.StorageService
import pl.jutupe.cartogobackend.storage.domain.model.VehicleImageFileResource
import pl.jutupe.cartogobackend.storage.domain.model.FileResource
import pl.jutupe.cartogobackend.storage.domain.model.VehicleDirectoryResource
import pl.jutupe.cartogobackend.user.domain.model.User
import pl.jutupe.cartogobackend.vehicle.application.model.VehicleRequest
import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle
import pl.jutupe.cartogobackend.vehicle.infrastructure.VehicleRepository

@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val storageService: StorageService,
) {
    fun create(user: User, request: VehicleRequest): Vehicle {
        val vehicle = Vehicle(
            rentalId = user.rental!!.id,
            registrationNumber = request.registrationNumber,
            name = request.name,
            image = null,
            state = Vehicle.State(
                mileage = request.state.mileage,
                fuelLevel = request.state.fuelLevel,
                condition = request.state.condition,
                location = request.state.location ?: Vehicle.State.Location.RENTAL,
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
                location = request.state.location ?: vehicle.state.location,
            ),
        ))
    }

    fun delete(vehicle: Vehicle) {
        vehicleRepository.delete(vehicle)

        storageService.removeDirectory(VehicleDirectoryResource(rentalId = vehicle.rentalId, vehicleId = vehicle.id))
    }

    fun getCarImageFileResource(vehicle: Vehicle): FileResource? {
        val nameWithExtension = vehicle.image?.substringAfterLast('/')
            ?: return null

        return VehicleImageFileResource(
            rentalId = vehicle.rentalId,
            vehicleId = vehicle.id,
            nameWithExtension = nameWithExtension,
        )
    }

    fun setImage(vehicle: Vehicle, image: FileResource?): Vehicle {
        return vehicleRepository.save(vehicle.copy(
            image = image?.pathWithName,
        ))
    }
}