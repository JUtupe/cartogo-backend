package pl.jutupe.cartogobackend.vehicle.infrastructure

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle

@Repository
interface VehicleRepository : CrudRepository<Vehicle, String> {
    fun findByRentalId(rentalId: String): List<Vehicle>
}