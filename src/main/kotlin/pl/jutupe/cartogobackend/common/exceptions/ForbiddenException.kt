package pl.jutupe.cartogobackend.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class ForbiddenException(reason: String? = null) : ResponseStatusException(HttpStatus.FORBIDDEN, reason)