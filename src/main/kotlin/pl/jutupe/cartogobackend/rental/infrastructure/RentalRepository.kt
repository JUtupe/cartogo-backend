package pl.jutupe.cartogobackend.rental.infrastructure

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pl.jutupe.cartogobackend.rental.domain.model.Rental
import pl.jutupe.cartogobackend.user.domain.model.User

@Repository
interface RentalRepository : CrudRepository<Rental, String> {
    fun findByUsersContaining(user: User): List<Rental>
}