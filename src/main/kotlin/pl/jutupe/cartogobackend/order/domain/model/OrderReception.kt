package pl.jutupe.cartogobackend.order.domain.model

import pl.jutupe.cartogobackend.common.Address
import pl.jutupe.cartogobackend.user.domain.model.User
import javax.persistence.*


@Entity
data class OrderReception(
    @Id
    val orderId: String,

    @ManyToOne
    val operator: User,

    @Column
    @Embedded
    val address: Address,

    @Column
    val customerSignature: String,
)
