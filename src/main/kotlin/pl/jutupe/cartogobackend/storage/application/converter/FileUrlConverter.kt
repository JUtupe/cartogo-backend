package pl.jutupe.cartogobackend.storage.application.converter

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.storage.application.model.FileUrlResponse
import pl.jutupe.cartogobackend.storage.domain.model.FileResource

@Component
class FileUrlConverter(
    @Value("\${storage.url}")
    private val storageServiceUrl: String
) {

    fun convert(file: FileResource): FileUrlResponse =
        convert(file.pathWithName)

    fun convert(pathWithName: String): FileUrlResponse {
        val fullFileUrl = "$storageServiceUrl/v1/storage/$pathWithName"

        return FileUrlResponse(fullFileUrl)
    }
}