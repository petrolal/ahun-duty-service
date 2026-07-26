package com.petrolal.ahun.ahundutyservice.application.ports

import com.petrolal.ahun.ahundutyservice.domain.Duty
import com.petrolal.ahun.ahundutyservice.domain.DutyTypeEnum
import java.util.UUID

/**
 * Outbound port interface for managing [Duty] persistence.
 * Implemented by database adapters to communicate with the database.
 */
interface DutyRepositoryPort {
    /**
     * Retrieves all duties from the database.
     *
     * @return List of all [Duty]s.
     */
    fun findAll(): List<Duty>

    /**
     * Retrieves all duties associated with a theme name.
     *
     * @param themeName The name of the theme.
     * @return List of matching [Duty]s.
     */
    fun findByThemeName(themeName: String): List<Duty>

    /**
     * Retrieves all duties matching a specific duty type.
     *
     * @param dutyType The [DutyTypeEnum] to filter by.
     * @return List of matching [Duty]s.
     */
    fun findByDutyType(dutyType: DutyTypeEnum): List<Duty>

    /**
     * Retrieves all duties matching a specific theme name and duty type.
     *
     * @param themeName The name of the theme.
     * @param dutyType The [DutyTypeEnum] to filter by.
     * @return List of matching [Duty]s.
     */
    fun findByThemeNameAndDutyType(
        themeName: String,
        dutyType: DutyTypeEnum,
    ): List<Duty>

    /**
     * Persists a new duty assignment.
     *
     * @param duty The [Duty] domain object to save.
     * @return The persisted [Duty] domain object.
     */
    fun create(duty: Duty): Duty

    /**
     * Retrieves a duty by its UUID.
     *
     * @param id The duty ID.
     * @return The [Duty] or null if not found.
     */
    fun findById(id: UUID): Duty?

    /**
     * Retrieves the duty for the specified duty type in the given month and year.
     *
     * @param dutyType The [DutyTypeEnum].
     * @param year The year of the duty.
     * @param month The month of the duty.
     * @return The matching [Duty] or null if not found.
     */
    fun findCurrentMonthDutyByType(
        dutyType: DutyTypeEnum,
        year: Int,
        month: Int,
    ): Duty?

    /**
     * Updates an existing duty assignment.
     *
     * @param id The UUID of the duty to update.
     * @param duty The updated [Duty] domain object.
     * @return The updated [Duty] domain object.
     */
    fun update(
        id: UUID,
        duty: Duty,
    ): Duty
}
