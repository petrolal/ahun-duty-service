package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.telegram

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TelegramUpdate(
    @JsonProperty("update_id") val updateId: Long? = null,
    val message: TelegramMessage? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TelegramMessage(
    @JsonProperty("message_id") val messageId: Long? = null,
    val chat: TelegramChat? = null,
    val text: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TelegramChat(
    val id: Long? = null,
    val title: String? = null,
    val type: String? = null
)
