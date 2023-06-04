package pl.jutupe.cartogobackend.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class NotFoundException(reason: String? = null) : ResponseStatusException(HttpStatus.NOT_FOUND, reason)