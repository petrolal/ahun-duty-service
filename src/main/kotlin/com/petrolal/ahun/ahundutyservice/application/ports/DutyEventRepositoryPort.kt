package com.petrolal.ahun.ahundutyservice.application.ports

import com.petrolal.ahun.ahundutyservice.domain.DutyEvent
import java.util.UUID

/**
 * Outbound port interface for managing [DutyEvent] persistence.
 * Implemented by database adapters to communicate with the database.
 */
interface DutyEventRepositoryPort {
    /**
     * Retrieves all duty events from the database.
     *
     * @return List of all [DutyEvent]s.
     */
    fun findAll(): List<DutyEvent>

    /**
     * Persists multiple new duty events.
     *
     * @param events The list of [DutyEvent]s to save.
     * @return The persisted list of [DutyEvent]s.
     */
    fun create(events: List<DutyEvent>): List<DutyEvent>

    /**
     * Retrieves multiple duty events matching the provided unique identifiers.
     *
     * @param ids List of duty event UUIDs.
     * @return List of matching [DutyEvent]s.
     */
    fun findAllById(ids: List<UUID>): List<DutyEvent>

    /**
     * Retrieves a duty event by its UUID.
     *
     * @param id The duty event UUID.
     * @return The [DutyEvent] or null if not found.
     */
    fun findById(id: UUID): DutyEvent?

    /**
     * Updates an existing duty event.
     *
     * @param id The UUID of the duty event to update.
     * @param event The updated [DutyEvent] domain object.
     * @return The updated [DutyEvent] domain object.
     */
    fun update(
        id: UUID,
        event: DutyEvent,
    ): DutyEvent
}
