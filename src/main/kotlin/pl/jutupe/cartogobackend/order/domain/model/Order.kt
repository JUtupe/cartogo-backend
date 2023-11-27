package pl.jutupe.cartogobackend.order.domain.model

import org.springframework.data.annotation.CreatedDate
import pl.jutupe.cartogobackend.rental.domain.model.Rental
import pl.jutupe.cartogobackend.vehicle.domain.model.Vehicle
import java.util.*
import javax.persistence.*

@Entity(name = "orders")
data class Order(
    @Id
    val id: String = UUID.randomUUID().toString(),
    @Column
    val number: String,
    @Column
    val amount: Double,
    @Column
    @Enumerated(EnumType.STRING)
    val paymentMethod: PaymentMethod,

    @Column
    val deliveryDate: Date,
    @Column
    val receptionDate: Date,

    @Column
    @Embedded
    val customer: Customer,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicleId", nullable = false)
    val vehicle: Vehicle,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rentalId", nullable = false)
    val rental: Rental,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    var delivery: OrderDelivery? = null,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    var reception: OrderReception? = null,
) {
    @CreatedDate
    @Column
    val createdDate: Date = Date()

    enum class PaymentMethod {
        CASH,
        CARD,
        TRANSFER,
    }

    @Embeddable
    data class Customer(
        @Column
        val firstName: String,

        @Column
        val lastName: String,

        @Column
        val email: String,

        @Column
        val phoneNumber: String,
    )
}
