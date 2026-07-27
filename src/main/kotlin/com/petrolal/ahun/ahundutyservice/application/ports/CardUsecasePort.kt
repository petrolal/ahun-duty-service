package com.petrolal.ahun.ahundutyservice.application.ports

import com.petrolal.ahun.ahundutyservice.domain.CardReturn
import java.util.*

/**
 * Inbound port interface for Card generation business use cases.
 */
interface CardUsecasePort {
    /**
     * Generates the HTML preview for a card based on a specific duty ID or the actual month's GIRA_ABERTA duty.
     *
     * @param dutyId Optional specific duty UUID to render.
     * @return Processed HTML content string.
     */
    fun generateCard(dutyId: UUID? = null, render: Boolean = false): CardReturn
}
