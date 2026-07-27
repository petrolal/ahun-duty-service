package com.petrolal.ahun.ahundutyservice.domain

import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * Domain model representing a Duty Event.
 * A Duty Event defines a schedule/action that occurs during a duty session.
 *
 * @property id Unique identifier of the duty event.
 * @property name The name of the event.
 * @property startedAt The scheduled time when this event starts.
 * @property visibleInCard Flag indicating if this event is visible inside the card view.
 * @property createdAt Timestamp when the event was created.
 * @property updatedAt Optional timestamp when the event was last updated.
 */
data class DutyEvent(
    var id: UUID,
    var name: String,
    var startedAt: LocalTime,
    var visibleInCard: Boolean,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime? = null,
)