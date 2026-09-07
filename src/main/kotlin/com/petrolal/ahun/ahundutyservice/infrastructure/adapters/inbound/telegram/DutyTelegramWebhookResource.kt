package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.telegram

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Telegram Webhook")
@RestController
@RequestMapping("/api/telegram")
class DutyTelegramWebhookResource(
    private val commandHandler: DutyTelegramCommandHandler
) {
    @PostMapping("/webhook")
    fun onWebhookUpdate(@RequestBody update: TelegramUpdate): ResponseEntity<Void> {
        commandHandler.handle(update)
        return ResponseEntity.ok().build()
    }
}
