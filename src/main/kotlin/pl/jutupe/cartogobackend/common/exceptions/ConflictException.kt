package pl.jutupe.cartogobackend.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class ConflictException(reason: String? = null) : ResponseStatusException(HttpStatus.CONFLICT, reason)