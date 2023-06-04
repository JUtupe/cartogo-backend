package pl.jutupe.cartogobackend.rental.domain.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class RentalInvitation(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column
    val rentalId: String,

    @Column(unique = true)
    val email: String,
)