package com.petrolal.ahun.ahundutyservice.application.usecases

import com.petrolal.ahun.ahundutyservice.application.ports.FileStoragePort
import com.petrolal.ahun.ahundutyservice.application.ports.TemplateRepositoryPort
import com.petrolal.ahun.ahundutyservice.application.ports.ThemeRepositoryPort
import com.petrolal.ahun.ahundutyservice.domain.Template
import com.petrolal.ahun.ahundutyservice.domain.exception.BadRequestException
import com.petrolal.ahun.ahundutyservice.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

/**
 * Application service orchestrating business logic for card background [Template]s.
 */
@Service
@Transactional(readOnly = true)
class TemplateUsecase(
    private val templateRepository: TemplateRepositoryPort,
    private val themeRepository: ThemeRepositoryPort,
    private val fileStoragePort: FileStoragePort,
) {

    fun findAll(): List<Template> = templateRepository.findAll()

    fun findById(id: UUID): Template =
        templateRepository.findById(id)
            ?: throw ResourceNotFoundException("Template with id $id not found")

    fun findByThemeId(themeId: UUID): List<Template> =
        templateRepository.findByThemeId(themeId)

    @Transactional
    fun create(name: String, themeId: UUID?, file: MultipartFile): Template {
        val imagePath = savePngFile(file)
        val theme = themeId?.let {
            themeRepository.findById(it) ?: throw ResourceNotFoundException("Theme with id $it not found")
        }

        val template = Template(
            id = UUID.randomUUID(),
            name = name,
            imagePath = imagePath,
            theme = theme
        )

        return templateRepository.save(template)
    }

    @Transactional
    fun update(id: UUID, name: String?, themeId: UUID?, file: MultipartFile?): Template {
        val existing = findById(id)

        val updatedImagePath = if (file != null && !file.isEmpty) {
            savePngFile(file)
        } else {
            existing.imagePath
        }

        val updatedTheme = if (themeId != null) {
            themeRepository.findById(themeId) ?: throw ResourceNotFoundException("Theme with id $themeId not found")
        } else {
            existing.theme
        }

        val updatedName = name?.takeIf { it.isNotBlank() } ?: existing.name

        val updatedTemplate = existing.copy(
            name = updatedName,
            imagePath = updatedImagePath,
            theme = updatedTheme
        )

        return templateRepository.update(id, updatedTemplate)
    }

    @Transactional
    fun delete(id: UUID) {
        val existing = findById(id)
        templateRepository.deleteById(id)

        if (!existing.imagePath.startsWith("gira_") && !existing.imagePath.startsWith("feijoada_")) {
            fileStoragePort.deleteImage(existing.imagePath)
        }
    }

    private fun savePngFile(file: MultipartFile): String {
        if (file.isEmpty) {
            throw BadRequestException("Uploaded PNG file cannot be empty")
        }

        val originalFilename = file.originalFilename ?: "template.png"
        if (!originalFilename.lowercase().endsWith(".png") && file.contentType != "image/png") {
            throw BadRequestException("Only PNG images are supported. File must be .png")
        }

        return fileStoragePort.saveImage(originalFilename, file.bytes)
    }
}
