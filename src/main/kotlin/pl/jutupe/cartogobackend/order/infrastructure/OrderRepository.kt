package pl.jutupe.cartogobackend.order.infrastructure

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pl.jutupe.cartogobackend.order.domain.model.Order

@Repository
interface OrderRepository : CrudRepository<Order, String> {
    fun findAllByRentalIdOrderByCreatedDate(rentalId: String): List<Order>
}
