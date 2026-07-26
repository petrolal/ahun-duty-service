package com.petrolal.ahun.ahundutyservice.domain.dto

import com.petrolal.ahun.ahundutyservice.domain.DutyTypeEnum
import com.petrolal.ahun.ahundutyservice.domain.SemesterEnum
import java.time.LocalDate
import java.util.*

/**
 * Data Transfer Object (DTO) for creating or updating a [com.petrolal.ahun.ahundutyservice.domain.Duty].
 *
 * @property themeId Unique identifier of the associated [com.petrolal.ahun.ahundutyservice.domain.Theme].
 * @property dutyType The type/category of the duty.
 * @property date The date when the duty is scheduled.
 * @property period Optional academic semester/period of the duty (automatically derived from date if omitted).
 * @property description Optional descriptive text about the duty.
 * @property eventIds List of unique identifiers of associated [com.petrolal.ahun.ahundutyservice.domain.DutyEvent]s.
 * @property inlineEvents Optional inline event objects to create and attach in a single transaction.
 */
data class DutyRequestDto(
    val themeId: UUID,
    val dutyType: DutyTypeEnum,
    val date: LocalDate,
    val period: SemesterEnum? = null,
    val description: String? = null,
    val eventIds: List<UUID> = emptyList(),
    val inlineEvents: List<DutyEventRequestDto>? = null,
)
