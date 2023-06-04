package pl.jutupe.cartogobackend.rental.domain.exceptions

import pl.jutupe.cartogobackend.common.exceptions.ConflictException

class UserAlreadyInRentalException(userId: String) : ConflictException("User $userId is already in another rental")