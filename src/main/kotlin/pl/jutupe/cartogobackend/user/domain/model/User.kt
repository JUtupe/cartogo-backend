package pl.jutupe.cartogobackend.user.domain.model

import org.springframework.data.annotation.CreatedDate
import pl.jutupe.cartogobackend.rental.domain.model.Rental
import java.util.Date
import java.util.UUID
import javax.persistence.*

@Entity
data class User(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(unique = true)
    val googleId: String,

    @Column
    val name: String,

    @Column(unique = true)
    val email: String,

    @Column(nullable = true)
    val avatar: String?,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rentalId", nullable = true)
    val rental: Rental? = null,
) {
    @CreatedDate
    @Column
    val createdDate: Date = Date()
}