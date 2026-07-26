package com.petrolal.ahun.ahundutyservice.application.ports

import com.petrolal.ahun.ahundutyservice.domain.Template
import java.util.UUID

/**
 * Outbound port interface for managing [Template] persistence.
 */
interface TemplateRepositoryPort {
    /**
     * Finds all templates.
     */
    fun findAll(): List<Template>

    /**
     * Finds a template by its ID.
     */
    fun findById(id: UUID): Template?

    /**
     * Finds all templates associated with a specific theme ID.
     */
    fun findByThemeId(themeId: UUID): List<Template>

    /**
     * Saves a new template.
     */
    fun save(template: Template): Template

    /**
     * Updates an existing template.
     */
    fun update(
        id: UUID,
        template: Template,
    ): Template

    /**
     * Deletes a template by ID.
     */
    fun deleteById(id: UUID)
}
