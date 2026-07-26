package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest

import com.petrolal.ahun.ahundutyservice.application.ports.CardUsecasePort
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
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
     * Endpoint to preview the card HTML for a specific duty ID.
     */
    @GetMapping("{dutyId}/preview")
    fun preview(
        @PathVariable("dutyId") dutyId: UUID,
    ): String = cardUsecase.getPreview(dutyId = dutyId)

    /**
     * Endpoint to render and export the card PNG image for a specific duty ID.
     */
    @GetMapping("{dutyId}/render", produces = [MediaType.IMAGE_PNG_VALUE])
    fun generateCard(
        @PathVariable("dutyId") dutyId: UUID,
    ): ResponseEntity<ByteArray> {
        val pngBytes = cardUsecase.renderCardPng(dutyId = dutyId)
        return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_PNG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"card-$dutyId.png\"")
            .body(pngBytes)
    }
}
