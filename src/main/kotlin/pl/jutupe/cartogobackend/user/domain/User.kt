package pl.jutupe.cartogobackend.user.domain

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class User(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column
    val name: String = "Jan " + UUID.randomUUID().toString(),
)