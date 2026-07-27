package com.petrolal.ahun.ahundutyservice.infrastructure.persistence.repository

import com.petrolal.ahun.ahundutyservice.application.ports.DutyEventRepositoryPort
import com.petrolal.ahun.ahundutyservice.domain.DutyEvent
import com.petrolal.ahun.ahundutyservice.infrastructure.persistence.entity.DutyEventEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Persistence database adapter implementing [DutyEventRepositoryPort] for database interaction.
 * Handles data mapping between [DutyEvent] domain models and [DutyEventEntity] database entities.
 */
@Repository
class DutyEventRepository(
    private val repository: DutyEventRepositoryJpa
) : DutyEventRepositoryPort {

    /**
     * Fetches all duty events and maps them to domain models.
     */
    override fun findAll(): List<DutyEvent> {
        return repository.findAll()
            .map(DutyEventEntity::toDomain)
    }

    /**
     * Saves a list of duty events to the database.
     */
    override fun create(events: List<DutyEvent>): List<DutyEvent> {
        return events.map { event ->
            val entity = DutyEventEntity.toEntity(event)
            DutyEventEntity.toDomain(repository.save(entity))
        }
    }

    /**
     * Finds multiple duty events by their unique identifiers.
     */
    override fun findAllById(ids: List<UUID>): List<DutyEvent> {
        return repository.findAllById(ids)
            .map(DutyEventEntity::toDomain)
    }

    /**
     * Finds a duty event by its ID.
     */
    override fun findById(id: UUID): DutyEvent? {
        return repository.findById(id)
            .map(DutyEventEntity::toDomain)
            .orElse(null)
    }

    /**
     * Updates an existing duty event in the database.
     */
    override fun update(id: UUID, event: DutyEvent): DutyEvent {
        val existingEntity = repository.findById(id)
            .orElseThrow { com.petrolal.ahun.ahundutyservice.domain.exception.ResourceNotFoundException("DutyEvent with id $id not found") }

        existingEntity.name = event.name
        existingEntity.startedAt = event.startedAt
        existingEntity.visibleInCard = event.visibleInCard
        existingEntity.updatedAt = java.time.LocalDateTime.now()

        val saved = repository.save(existingEntity)
        return DutyEventEntity.toDomain(saved)
    }
}