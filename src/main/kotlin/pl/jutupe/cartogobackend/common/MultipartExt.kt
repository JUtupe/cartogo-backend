package pl.jutupe.cartogobackend.common

import org.springframework.web.multipart.MultipartFile

val MultipartFile.extension: String?
    get() {
        val originalFilename = originalFilename ?: return ""
        val lastDotIndex = originalFilename.lastIndexOf('.')

        return if (lastDotIndex == -1) {
            null
        } else {
            originalFilename.substring(lastDotIndex + 1)
        }
    }