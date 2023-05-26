package pl.jutupe.cartogobackend.user.infrastructure

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pl.jutupe.cartogobackend.user.domain.User

@Repository
interface UserRepository : CrudRepository<User, String> {
    fun getAllByNameStartsWith(name: String): List<User>
}