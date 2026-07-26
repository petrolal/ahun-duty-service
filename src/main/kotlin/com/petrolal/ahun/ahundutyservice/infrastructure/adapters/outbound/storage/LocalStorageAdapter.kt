package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.outbound.storage

import com.petrolal.ahun.ahundutyservice.application.ports.FileStoragePort
import com.petrolal.ahun.ahundutyservice.domain.exception.BadRequestException
import com.petrolal.ahun.ahundutyservice.domain.exception.ResourceNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

/**
 * Outbound adapter implementing [FileStoragePort] for local disk and classpath asset storage.
 */
@Component
class LocalStorageAdapter(
    @Value("\${app.storage.directory:uploads/images}")
    private val storageDirSetting: String
) : FileStoragePort {

    override fun saveImage(originalFilename: String, bytes: ByteArray): String {
        if (bytes.isEmpty()) {
            throw BadRequestException("Uploaded PNG file cannot be empty")
        }

        val sanitizedFilename = sanitizeFilename(originalFilename)
        val uniqueFilename = "${UUID.randomUUID().toString().substring(0, 8)}_$sanitizedFilename"

        val targetDir = Paths.get(storageDirSetting).toAbsolutePath().normalize()
        Files.createDirectories(targetDir)

        val targetFile = targetDir.resolve(uniqueFilename)
        Files.write(targetFile, bytes)

        return uniqueFilename
    }

    override fun loadImageAsBytes(imageName: String): ByteArray {
        // 1. Check external storage directory
        val targetPath = Paths.get(storageDirSetting).toAbsolutePath().normalize().resolve(imageName)
        if (Files.exists(targetPath) && Files.isRegularFile(targetPath)) {
            return Files.readAllBytes(targetPath)
        }

        // 2. Fallback to classpath static resources
        val resource = ClassPathResource("static/images/$imageName")
        if (resource.exists()) {
            return resource.inputStream.readAllBytes()
        }

        throw ResourceNotFoundException("Background image file '$imageName' could not be found on server disk or classpath")
    }

    override fun deleteImage(imageName: String): Boolean {
        try {
            val targetPath = Paths.get(storageDirSetting).toAbsolutePath().normalize().resolve(imageName)
            if (Files.exists(targetPath) && Files.isRegularFile(targetPath)) {
                return Files.deleteIfExists(targetPath)
            }
        } catch (_: Exception) {
            // Ignore deletion failures
        }
        return false
    }

    private fun sanitizeFilename(filename: String): String {
        val nameWithoutExt = filename.removeSuffix(".png").removeSuffix(".PNG")
        val clean = nameWithoutExt.lowercase().replace(Regex("[^a-z0-9_]+"), "_").trim('_')
        return if (clean.isBlank()) "template.png" else "$clean.png"
    }
}
