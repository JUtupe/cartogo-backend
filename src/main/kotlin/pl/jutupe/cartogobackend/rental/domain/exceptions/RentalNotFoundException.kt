package pl.jutupe.cartogobackend.rental.domain.exceptions

import pl.jutupe.cartogobackend.common.exceptions.NotFoundException

class RentalNotFoundException : NotFoundException {
    constructor() : super("Rental not found")
    constructor(rentalId: String) : super("Rental $rentalId not found")
}