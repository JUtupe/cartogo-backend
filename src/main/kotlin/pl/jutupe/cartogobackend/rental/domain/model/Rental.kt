package pl.jutupe.cartogobackend.rental.domain.model

import org.springframework.data.annotation.CreatedDate
import pl.jutupe.cartogobackend.common.Address
import java.util.*
import javax.persistence.*

@Entity
data class Rental(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column
    val name: String,

    @Column
    val nip: String,

    @Column
    @Embedded
    val address: Address,

    @Column
    @Embedded
    val owner: Owner,

    @Column(unique = true)
    val ownerId: String,

    @OneToMany(mappedBy = "rentalId", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val invites: MutableSet<RentalInvitation>,
) {
    @CreatedDate
    @Column
    val createdDate: Date = Date()

    @Embeddable
    data class Owner(
        @Column
        val firstName: String,

        @Column
        val lastName: String,
    )
}