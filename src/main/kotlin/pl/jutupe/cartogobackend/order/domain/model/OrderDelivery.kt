package pl.jutupe.cartogobackend.order.domain.model

import pl.jutupe.cartogobackend.common.Address
import pl.jutupe.cartogobackend.user.domain.model.User
import javax.persistence.*

@Entity
data class OrderDelivery(
    @Id
    val orderId: String,

    @ManyToOne
    val operator: User,

    @Column(nullable = true)
    val pesel: String?,
    @Column(nullable = true)
    val nip: String?,

    @Column
    val invoiceData: String,
    @Column
    val drivingLicenseNumber: String,
    @Column
    val idNumber: String,

    @Column
    @Embedded
    val address: Address,

    @Column
    val customerSignature: String,
)
