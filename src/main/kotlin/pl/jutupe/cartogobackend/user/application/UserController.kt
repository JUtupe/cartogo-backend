package pl.jutupe.cartogobackend.user.application

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.jutupe.cartogobackend.auth.domain.UserPrincipal
import pl.jutupe.cartogobackend.common.extension
import pl.jutupe.cartogobackend.storage.domain.StorageService
import pl.jutupe.cartogobackend.storage.domain.model.UserSignatureFileResource
import pl.jutupe.cartogobackend.user.application.converter.UserConverter
import pl.jutupe.cartogobackend.user.application.model.UserResponse
import pl.jutupe.cartogobackend.user.domain.UserService
import java.util.*

@RestController
@RequestMapping("v1/users")
class UserController(
    private val storageService: StorageService,
    private val userService: UserService,
    private val userConverter: UserConverter,
) {

    @PostMapping(consumes = ["multipart/form-data", "application/json"])
    @PutMapping("@me/signature")
    fun putSignature(
        @RequestPart("signature", required = false) signature: MultipartFile?,
        @AuthenticationPrincipal principal: UserPrincipal
    ): UserResponse {
        userService.getSignatureFileResource(principal.user)?.let {
            storageService.removeResource(it)
        }

        val signatureResource = signature?.let {
            val resource = UserSignatureFileResource(
                userId = principal.user.id,
                nameWithExtension = (UUID.randomUUID().toString() + '.' + it.extension),
            )

            storageService.saveImage(it, resource)
        }

        val user = userService.setSignature(principal.user, signatureResource)

        return userConverter.toResponse(user)
    }
}