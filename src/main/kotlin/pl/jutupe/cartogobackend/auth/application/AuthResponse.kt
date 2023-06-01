package pl.jutupe.cartogobackend.auth.application

import pl.jutupe.cartogobackend.user.application.model.UserResponse

data class AuthResponse(
    val accessToken: String,
    val user: UserResponse,
)