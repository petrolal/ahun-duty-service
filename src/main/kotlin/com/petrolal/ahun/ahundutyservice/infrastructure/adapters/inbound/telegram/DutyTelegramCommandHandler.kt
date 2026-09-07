package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.telegram

import com.petrolal.ahun.ahundutyservice.application.ports.CardUsecasePort
import com.petrolal.ahun.ahundutyservice.application.usecases.DutyUsecase
import com.petrolal.ahun.ahundutyservice.domain.CardReturn
import com.petrolal.ahun.ahundutyservice.infrastructure.adapters.outbound.telegram.TelegramClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class DutyTelegramCommandHandler(
    private val dutyUsecase: DutyUsecase,
    private val cardUsecase: CardUsecasePort,
    private val telegramClient: TelegramClient
) {
    private val log = LoggerFactory.getLogger(DutyTelegramCommandHandler::class.java)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun handle(update: TelegramUpdate) {
        val text = update.message?.text?.trim() ?: return
        val chatId = update.message.chat?.id?.toString() ?: return
        val command = text.split(Regex("\\s+"))[0].split("@")[0].lowercase()

        log.info("Handling duty telegram command: {} from chatId: {}", command, chatId)

        when (command) {
            "/funcao", "/proxima_funcao" -> handleFuncao(chatId)
            "/cartao_funcao" -> handleCartaoFuncao(chatId)
            else -> log.debug("Unhandled duty command: {}", command)
        }
    }

    private fun handleFuncao(chatId: String) {
        val duties = dutyUsecase.findAll()
        val today = LocalDate.now()
        val nextDuty = duties.filter { !it.date.isBefore(today) }.minByOrNull { it.date }
            ?: duties.maxByOrNull { it.date }

        if (nextDuty == null) {
            telegramClient.sendMessage(chatId, "🌿 Nenhuma função agendada no momento.")
            return
        }

        val sb = StringBuilder()
        sb.append("🌿 *Próxima Função / Gira*\n\n")
        sb.append("🎯 *Tema:* ${nextDuty.theme.name}\n")
        sb.append("📅 *Data:* ${nextDuty.date.format(dateFormatter)}\n")
        sb.append("🏷️ *Tipo:* ${nextDuty.dutyType.name}\n")

        if (nextDuty.events.isNotEmpty()) {
            sb.append("\n⏰ *Programação:*\n")
            nextDuty.events.sortedBy { it.startedAt }.forEach { event ->
                sb.append("• ${event.startedAt} - ${event.name}\n")
            }
        }

        telegramClient.sendMessage(chatId, sb.toString())
    }

    private fun handleCartaoFuncao(chatId: String) {
        telegramClient.sendMessage(chatId, "🎨 Gerando o cartão da função, aguarde um instante...")
        try {
            val duties = dutyUsecase.findAll()
            val today = LocalDate.now()
            val nextDuty = duties.filter { !it.date.isBefore(today) }.minByOrNull { it.date }
                ?: duties.maxByOrNull { it.date }

            val cardReturn = cardUsecase.generateCard(dutyId = nextDuty?.id, render = true)
            when (cardReturn) {
                is CardReturn.Render -> {
                    val caption = nextDuty?.let { "Cartão da Gira: ${it.theme.name} (${it.date.format(dateFormatter)})" } ?: "Cartão da Gira"
                    telegramClient.sendPhoto(chatId, cardReturn.png, "cartao-gira.png", caption)
                }
                is CardReturn.Preview -> {
                    telegramClient.sendMessage(chatId, "⚠️ Erro ao renderizar a imagem do cartão.")
                }
            }
        } catch (e: Exception) {
            log.error("Failed to generate card for Telegram: {}", e.message, e)
            telegramClient.sendMessage(chatId, "❌ Não foi possível gerar o cartão da função: ${e.message}")
        }
    }
}
