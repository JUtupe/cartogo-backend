package pl.jutupe.cartogobackend.common

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Address(
    @Column
    val postalCode: String,
    @Column
    val street: String,
    @Column
    val city: String,
)
