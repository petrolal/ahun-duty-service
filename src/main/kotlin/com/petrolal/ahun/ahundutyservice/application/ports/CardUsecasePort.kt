package com.petrolal.ahun.ahundutyservice.application.ports

import java.util.UUID

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
    fun getPreview(dutyId: UUID): String

    /**
     * Renders a PNG image byte array for a card based on a specific duty ID or the actual month's GIRA_ABERTA duty.
     *
     * @param dutyId Optional specific duty UUID to render.
     * @return PNG image byte array.
     */
    fun renderCardPng(dutyId: UUID): ByteArray
}
