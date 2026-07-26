package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest

import com.petrolal.ahun.ahundutyservice.application.usecases.TemplateUsecase
import com.petrolal.ahun.ahundutyservice.domain.Template
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

/**
 * Inbound REST controller for managing Card Templates and PNG image uploads.
 */
@Tag(name = "Templates", description = "Endpoints to manage card background image templates")
@RestController
@RequestMapping("templates")
class TemplateResource(
    private val templateUsecase: TemplateUsecase
) {

    /**
     * List all templates, optionally filtered by theme ID query parameter.
     */
    @GetMapping
    fun list(
        @RequestParam(name = "themeId", required = false) themeId: UUID? = null,
    ): List<Template> {
        if (themeId != null) {
            return templateUsecase.findByThemeId(themeId)
        }
        return templateUsecase.findAll()
    }

    /**
     * Get a specific template by ID.
     */
    @GetMapping("/{id}")
    fun findById(
        @PathVariable("id") id: UUID,
    ): Template = templateUsecase.findById(id)

    /**
     * Create/Upload a new card template with a PNG background image.
     */
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun create(
        @RequestParam("name") name: String,
        @RequestParam(name = "themeId", required = false) themeId: UUID?,
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<Template> {
        val created = templateUsecase.create(name = name, themeId = themeId, file = file)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Update an existing card template and optional PNG background image.
     * Accepts ID as either path variable (`/templates/{id}`) or query parameter (`/templates?id=...`).
     */
    @PutMapping(value = ["/{id}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun update(
        @PathVariable(name = "id") id: UUID,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "themeId", required = false) themeId: UUID?,
        @RequestParam(name = "file", required = false) file: MultipartFile?,
    ): Template = templateUsecase.update(id = id, name = name, themeId = themeId, file = file)

    /**
     * Delete a template by ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable("id") id: UUID,
    ) = templateUsecase.delete(id)
}
