package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest

import com.petrolal.ahun.ahundutyservice.application.ports.CardUsecasePort
import com.petrolal.ahun.ahundutyservice.domain.CardReturn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

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
    ): String {
        return when (val cardReturn = cardUsecase.generateCard(dutyId = dutyId, render = false)) {
            is CardReturn.Preview -> cardReturn.html
            is CardReturn.Render -> error("Unexpected render result type for card preview")
        }
    }

    /**
     * Endpoint to render and export the card PNG image for a specific duty ID.
     */
    @GetMapping("{dutyId}/render", produces = [MediaType.IMAGE_PNG_VALUE])
    fun generateCard(
        @PathVariable("dutyId") dutyId: UUID,
    ): ResponseEntity<ByteArray> {
        return when (val cardReturn = cardUsecase.generateCard(dutyId = dutyId, render = true)) {
            is CardReturn.Render -> ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"card-$dutyId.png\"")
                .body(cardReturn.png)
            is CardReturn.Preview -> error("Unexpected preview result type for card render")
        }
    }
}
