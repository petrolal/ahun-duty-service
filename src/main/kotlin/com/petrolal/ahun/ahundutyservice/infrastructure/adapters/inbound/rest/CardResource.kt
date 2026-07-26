package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest

import com.petrolal.ahun.ahundutyservice.application.ports.CardUsecasePort
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Inbound REST controller adapter for managing and generating card image previews and PNGs.
 * Follows Hexagonal Architecture by delegating domain logic to [CardUsecasePort].
 */
@Tag(name = "Cards", description = "Endpoint to organize and render cards")
@RestController
@RequestMapping("cards")
class CardResource(
    private val cardUsecase: CardUsecasePort
) {

    /**
     * Endpoint to preview the card HTML.
     * Accepts duty ID as either path variable (`/cards/preview/{dutyId}`) or query parameter (`/cards/preview?dutyId=...`).
     * Defaults to the actual month's GIRA_ABERTA duty if no duty ID is specified.
     */
    @GetMapping(value = ["{dutyId}/preview"])
    fun preview(
        @RequestParam(name = "dutyId") dutyId: UUID,
    ): String = cardUsecase.getPreview(dutyId = dutyId)

    /**
     * Endpoint to render and export the card PNG image.
     * Accepts duty ID as either path variable (`/cards/render/{dutyId}`) or query parameter (`/cards/render?dutyId=...`).
     * Defaults to the actual month's GIRA_ABERTA duty if no duty ID is specified.
     */
    @GetMapping(value = ["{dutyId}/render"], produces = [MediaType.IMAGE_PNG_VALUE])
    fun generateCard(
        @PathVariable(name = "dutyId", required = false) dutyId: UUID,
    ): ResponseEntity<ByteArray> {
        val pngBytes = cardUsecase.renderCardPng(dutyId = dutyId)
        return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(pngBytes)
    }
}
