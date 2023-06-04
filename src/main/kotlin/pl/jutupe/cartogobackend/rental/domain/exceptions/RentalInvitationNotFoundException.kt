package pl.jutupe.cartogobackend.rental.domain.exceptions

import pl.jutupe.cartogobackend.common.exceptions.NotFoundException

class RentalInvitationNotFoundException(invitationId: String) : NotFoundException("Rental invitation $invitationId not found")