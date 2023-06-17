package pl.jutupe.cartogobackend.vehicle.infrastructure

import org.springframework.data.repository.CrudRepository
import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle

interface VehicleRepository : CrudRepository<Vehicle, String> {
    fun findByRentalId(rentalId: String): List<Vehicle>
}