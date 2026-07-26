package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest

import com.petrolal.ahun.ahundutyservice.application.usecases.DutyEventUsecase
import com.petrolal.ahun.ahundutyservice.domain.DutyEvent
import com.petrolal.ahun.ahundutyservice.domain.dto.DutyEventRequestDto
import com.petrolal.ahun.ahundutyservice.domain.exception.BadRequestException
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Inbound REST controller for managing Duty Events.
 * Exposes endpoints for listing, finding, creating, and updating duty events.
 */
@Tag(name = "Duty Events", description = "Programmatically events will happen at Duty")
@RestController
@RequestMapping("duty-events")
class DutyEventResource(
    private val dutyEventUsecase: DutyEventUsecase
) {

    /**
     * Endpoint to list all duty events.
     */
    @GetMapping
    fun findAll(): List<DutyEvent> = dutyEventUsecase.findAll()

    /**
     * Endpoint to get a specific duty event by ID.
     */
    @GetMapping("/{id}")
    fun findById(
        @PathVariable("id") id: UUID,
    ): DutyEvent = dutyEventUsecase.findById(id)

    /**
     * Endpoint to create one or more new duty events.
     */
    @PostMapping
    fun create(
        @RequestBody event: List<DutyEventRequestDto>,
    ): List<DutyEvent> = dutyEventUsecase.save(event)

    /**
     * Endpoint to update an existing duty event.
     * Accepts ID as either path variable (`/duty-events/{id}`) or query parameter (`/duty-events?id=...`).
     */
    @PutMapping(value = ["/{id}"])
    fun update(
        @PathVariable(name = "id", required = false) id: UUID,
        @RequestBody requestDto: DutyEventRequestDto,
    ): DutyEvent = dutyEventUsecase.update(id, requestDto)
}
