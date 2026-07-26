package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest

import com.petrolal.ahun.ahundutyservice.application.usecases.CardUsecase
import com.petrolal.ahun.ahundutyservice.application.usecases.DutyUsecase
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Inbound REST controller adapter for Telegram Bot webhooks.
 * Implements low-cost interactive channel frontend handling for Ahun Duty Service.
 */
@Tag(name = "Telegram Bot Channel", description = "Webhook receiver for Telegram Bot UI interaction")
@RestController
@RequestMapping("/telegram")
class TelegramWebhookResource(
    private val dutyUsecase: DutyUsecase,
    private val cardUsecase: CardUsecase
) {

    @PostMapping("/webhook")
    fun handleWebhookUpdate(@RequestBody updatePayload: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        val message = updatePayload["message"] as? Map<*, *>
        val text = message?.get("text") as? String

        val responseText = when {
            text?.startsWith("/start") == true -> "Welcome to Ahun Duty Bot! Use /duties to list scheduled duties or /card to render announcement cards."
            text?.startsWith("/duties") == true -> {
                val duties = dutyUsecase.findAll()
                if (duties.isEmpty()) "No duties scheduled."
                else "Scheduled Duties:\n" + duties.joinToString("\n") { "• ${it.date}: ${it.theme.name} (${it.dutyType})" }
            }
            text?.startsWith("/card") == true -> "Send a GET request to /cards/{dutyId}/render to fetch high-res announcement PNG card."
            else -> "Send /start for available commands."
        }

        val responseMap = mutableMapOf<String, Any>()
        responseMap["method"] = "sendMessage"
        (message?.get("chat") as? Map<*, *>)?.get("id")?.let { responseMap["chat_id"] = it }
        responseMap["text"] = responseText

        return ResponseEntity.ok(responseMap)
    }
}
