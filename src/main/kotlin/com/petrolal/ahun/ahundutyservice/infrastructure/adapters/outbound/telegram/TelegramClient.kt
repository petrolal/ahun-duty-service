package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.outbound.telegram

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Component
class TelegramClient(
    @Value("\${telegram.bot-token:}") private val botToken: String
) {
    private val log = LoggerFactory.getLogger(TelegramClient::class.java)
    private val restClient = RestClient.builder().baseUrl("https://api.telegram.org").build()

    fun sendMessage(chatId: String, text: String) {
        if (botToken.isBlank()) {
            log.warn("TELEGRAM_BOT_TOKEN is blank. Cannot send message to {}", chatId)
            return
        }
        try {
            restClient.post()
                .uri("/bot$botToken/sendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapOf("chat_id" to chatId, "text" to text, "parse_mode" to "Markdown"))
                .retrieve()
                .toBodilessEntity()
        } catch (e: Exception) {
            log.error("Failed to send Telegram message to {}: {}", chatId, e.message, e)
        }
    }

    fun sendPhoto(chatId: String, photoBytes: ByteArray, filename: String, caption: String?) {
        if (botToken.isBlank()) {
            log.warn("TELEGRAM_BOT_TOKEN is blank. Cannot send photo to {}", chatId)
            return
        }
        try {
            val body = LinkedMultiValueMap<String, Any>()
            body.add("chat_id", chatId)
            if (!caption.isNullOrBlank()) {
                body.add("caption", caption)
            }
            val photoResource = object : ByteArrayResource(photoBytes) {
                override fun getFilename(): String = filename
            }
            body.add("photo", photoResource)

            restClient.post()
                .uri("/bot$botToken/sendPhoto")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toBodilessEntity()
        } catch (e: Exception) {
            log.error("Failed to send Telegram photo to {}: {}", chatId, e.message, e)
        }
    }
}
