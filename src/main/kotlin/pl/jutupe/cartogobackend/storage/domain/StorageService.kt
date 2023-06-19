package pl.jutupe.cartogobackend.storage.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pl.jutupe.cartogobackend.common.exceptions.FileTypeNotSupportedException
import pl.jutupe.cartogobackend.storage.domain.model.DirectoryResource
import pl.jutupe.cartogobackend.storage.domain.model.FileResource
import pl.jutupe.cartogobackend.common.extension
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.*

@Service
class StorageService(
    @Value("\${storage.main-path}")
    private val mainStoragePath: String,
) {

    private val storageRoot = Files.createDirectories(Path(mainStoragePath))

    fun saveImage(fileItem: MultipartFile, targetResource: FileResource) {
        storageRoot.resolve(targetResource.storagePath).createDirectories()

        assertFileSize(fileItem.size)
        assertSupportedImageType(fileItem.extension ?: throw FileTypeNotSupportedException("null", ALLOWED_IMAGE_TYPES))

        val image = fileItem.inputStream.use { inp ->
            // ImageIO strips image metadata on read call
            ImageIO.read(inp)
                ?: throw FileTypeNotSupportedException("null", ALLOWED_IMAGE_TYPES)
        }
        val targetPath = storageRoot.resolve(targetResource.pathWithName)

        ImageIO.write(image, targetPath.extension, targetPath.toFile())
    }

    fun removeResource(resource: FileResource) {
        assertPathInStorage(storageRoot.resolve(resource.pathWithName))

        storageRoot.resolve(resource.pathWithName).deleteIfExists()
    }

    fun removeDirectory(resource: DirectoryResource) {
        val path: Path = storageRoot.resolve(resource.path).normalize()

        assertPathInStorage(path)

        runCatching {
            Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map { it.toFile() }
                .forEach { it.delete() }
        }
    }

    fun getFile(stringPath: String): File? {
        val path = Path(stringPath).normalize()
        val file = File(storageRoot.toFile(), path.toString())

        // Make sure file is somewhere in the storage root
        if (!file.canonicalPath.startsWith(storageRoot.toFile().canonicalPath)) {
            return null
        }
        if (!file.exists() || file.isDirectory) {
            return null
        }

        return file
    }

    private fun assertPathInStorage(path: Path) {
        if (!path.normalize().toFile().canonicalPath.startsWith(storageRoot.toFile().canonicalPath)) {
            throw SecurityException("Requested path should not be accessible")
        }
    }

    private fun assertSupportedImageType(extension: String) {
        if (extension.lowercase() !in ALLOWED_IMAGE_TYPES) {
            throw FileTypeNotSupportedException(extension, ALLOWED_IMAGE_TYPES)
        }
    }

    private fun assertFileSize(contentLength: Long) {
        if (contentLength >= MAX_FILE_SIZE) {
            throw SecurityException("File is too big ($contentLength/$MAX_FILE_SIZE)")
        }
    }

    companion object {
        private const val MAX_FILE_SIZE = 10_485_760 // 10MB

        private val ALLOWED_IMAGE_TYPES = listOf(
            "jpg",
            "png",
            "jpeg",
            "webp",
        )
    }

}