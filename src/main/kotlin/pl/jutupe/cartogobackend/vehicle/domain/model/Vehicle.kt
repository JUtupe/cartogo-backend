package pl.jutupe.cartogobackend.vehicle.domain.model

import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Id

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

        @Column
        val condition: Condition,
    ) {
        enum class Condition {
            CLEAN,
            SLIGHTLY_DIRTY,
            DIRTY
        }
    }
}