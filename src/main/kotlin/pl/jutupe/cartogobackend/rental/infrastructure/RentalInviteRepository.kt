package pl.jutupe.cartogobackend.rental.infrastructure

import org.springframework.data.repository.CrudRepository
import pl.jutupe.cartogobackend.rental.domain.model.RentalInvitation

interface RentalInviteRepository : CrudRepository<RentalInvitation, String> {
    fun findByEmail(email: String): RentalInvitation?
}