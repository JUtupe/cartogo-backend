package pl.jutupe.cartogobackend.rental.infrastructure

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pl.jutupe.cartogobackend.rental.domain.model.Rental

@Repository
interface RentalRepository : CrudRepository<Rental, String> {
    fun findByUserIdsContaining(userId: String): List<Rental>
}