package pl.jutupe.cartogobackend.user.application.model

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String?,
)