package pl.jutupe.cartogobackend.order.domain.exception

import pl.jutupe.cartogobackend.common.exceptions.NotFoundException

class OrderNotFoundException(orderId: String) : NotFoundException("Order with id $orderId not found")
