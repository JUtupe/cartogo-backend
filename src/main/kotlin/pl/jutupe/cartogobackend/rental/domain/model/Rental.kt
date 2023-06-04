package pl.jutupe.cartogobackend.rental.domain.model

import org.springframework.data.annotation.CreatedDate
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

    @Column
    @ElementCollection
    val userIds: MutableSet<String>,

    @OneToMany(mappedBy = "rentalId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val invites: MutableList<RentalInvitation>,
) {
    @CreatedDate
    @Column
    val createdDate: Date = Date()

    data class Address(
        @Column
        val postalCode: String,

        @Column
        val street: String,

        @Column
        val city: String,
    )

    data class Owner(
        @Column
        val firstName: String,

        @Column
        val lastName: String,
    )
}