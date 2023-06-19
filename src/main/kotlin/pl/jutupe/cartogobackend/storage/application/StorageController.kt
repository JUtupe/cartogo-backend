package pl.jutupe.cartogobackend.storage.application

import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.jutupe.cartogobackend.storage.domain.StorageService
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("v1/storage")
class StorageController(
    private val storageService: StorageService,
) {

    @GetMapping("**")
    fun getFile(
        request: HttpServletRequest,
    ): ResponseEntity<UrlResource> {
        val name = request.requestURI.split(request.contextPath + "/v1/storage/")[1]
        val image = storageService.getFile(name) ?: return ResponseEntity.notFound().build()

        val contentType = runCatching {
            request.servletContext.getMimeType(image.absolutePath)
        }.getOrDefault("application/octet-stream")

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(CONTENT_DISPOSITION, "attachment; filename=\"${image.name}\"")
            .body(UrlResource(image.toURI()))
    }
}