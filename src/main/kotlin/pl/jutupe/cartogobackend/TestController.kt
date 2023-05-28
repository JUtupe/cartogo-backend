package pl.jutupe.cartogobackend

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.jutupe.cartogobackend.user.domain.User
import pl.jutupe.cartogobackend.user.infrastructure.UserRepository

@RestController
@RequestMapping("v1/test")
class TestController(
    private val userRepository: UserRepository
) {

    @GetMapping
    fun test(): User {
        val user = User()

        userRepository.save(user)

        return user
    }

    @GetMapping("2")
    fun hehe() = "hehe"
}