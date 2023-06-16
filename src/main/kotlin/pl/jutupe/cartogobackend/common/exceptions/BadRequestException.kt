package pl.jutupe.cartogobackend.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class BadRequestException(reason: String? = null) : ResponseStatusException(HttpStatus.BAD_REQUEST, reason)