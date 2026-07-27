package com.petrolal.ahun.ahundutyservice.domain.dto

import java.time.LocalTime

/**
 * Data Transfer Object (DTO) for creating a [com.petrolal.ahun.ahundutyservice.domain.DutyEvent].
 *
 * @property name The name of the event.
 * @property startedAt The scheduled time when this event starts.
 * @property description Optional descriptive text about the event.
 * @property visibleInCard Flag indicating if this event is visible inside the card view.
 */
data class DutyEventRequestDto(
    var name: String,
    var startedAt: LocalTime,
    var visibleInCard: Boolean,
)
