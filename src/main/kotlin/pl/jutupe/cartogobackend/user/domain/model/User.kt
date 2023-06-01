package pl.jutupe.cartogobackend.user.domain.model

import org.springframework.data.annotation.CreatedDate
import java.util.Date
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

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
) {
    @CreatedDate
    @Column
    val createdDate: Date = Date()
}