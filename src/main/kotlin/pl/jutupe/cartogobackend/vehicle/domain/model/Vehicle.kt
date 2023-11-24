package pl.jutupe.cartogobackend.vehicle.domain.model

import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*

@Entity
data class Vehicle(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column
    val rentalId: String,

    @Column(unique = true)
    val registrationNumber: String,

    @Column
    val name: String,

    @Column
    val image: String?,

    @Column
    @Embedded
    val state: State
) {
    @CreatedDate
    @Column
    val createdDate: Date = Date()

    @Embeddable
    data class State(
        @Column
        val mileage: Long,

        @Column
        val fuelLevel: Int, // 0-100

        @Column(name="vehicle_condition")
        @Enumerated(EnumType.STRING)
        val condition: Condition,

        @Column(name="vehicle_location")
        @Enumerated(EnumType.STRING)
        val location: Location = Location.RENTAL,
    ) {
        enum class Condition {
            CLEAN,
            SLIGHTLY_DIRTY,
            DIRTY
        }

        enum class Location {
            RENTAL,
            CUSTOMER,
            SERVICE,
        }
    }
}