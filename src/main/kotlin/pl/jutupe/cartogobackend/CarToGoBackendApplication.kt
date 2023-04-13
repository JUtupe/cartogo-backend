package pl.jutupe.cartogobackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class CarToGoBackendApplication

fun main(args: Array<String>) {
    runApplication<CarToGoBackendApplication>(*args)
}
