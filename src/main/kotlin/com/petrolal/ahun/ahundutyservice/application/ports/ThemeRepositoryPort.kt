package com.petrolal.ahun.ahundutyservice.application.ports

import com.petrolal.ahun.ahundutyservice.domain.Theme
import java.util.UUID

/**
 * Outbound port interface for managing [Theme] persistence.
 * Implemented by database adapters to communicate with the database.
 */
interface ThemeRepositoryPort {
    /**
     * Retrieves all themes from the database.
     *
     * @return List of all [Theme]s.
     */
    fun findAll(): List<Theme>

    /**
     * Filters themes by their name.
     *
     * @param name The name snippet to query.
     * @return List of matching [Theme]s.
     */
    fun filterByName(name: String): List<Theme>

    /**
     * Persists a new theme.
     *
     * @param theme The [Theme] to be created.
     * @return The persisted [Theme] with generated database state.
     */
    fun create(theme: Theme): Theme

    /**
     * Updates an existing theme.
     *
     * @param id Unique identifier of the theme to update.
     * @param theme The updated theme information.
     * @return The updated [Theme].
     */
    fun update(
        id: UUID,
        theme: Theme,
    ): Theme

    /**
     * Looks up a theme by its unique identifier.
     *
     * @param id The theme UUID.
     * @return The [Theme] if found, or null otherwise.
     */
    fun findById(id: UUID): Theme?
}
