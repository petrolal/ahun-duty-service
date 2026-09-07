package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.telegram

import com.petrolal.ahun.ahundutyservice.application.ports.CardUsecasePort
import com.petrolal.ahun.ahundutyservice.application.usecases.DutyUsecase
import com.petrolal.ahun.ahundutyservice.domain.*
import com.petrolal.ahun.ahundutyservice.infrastructure.adapters.outbound.telegram.TelegramClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DutyTelegramCommandHandlerTest {

    private lateinit var dutyUsecase: DutyUsecase
    private lateinit var cardUsecase: CardUsecasePort
    private lateinit var telegramClient: TelegramClient
    private lateinit var handler: DutyTelegramCommandHandler

    @BeforeEach
    fun setUp() {
        dutyUsecase = mock()
        cardUsecase = mock()
        telegramClient = mock()
        handler = DutyTelegramCommandHandler(dutyUsecase, cardUsecase, telegramClient)
    }

    private fun createUpdate(text: String): TelegramUpdate {
        return TelegramUpdate(
            updateId = 1L,
            message = TelegramMessage(
                messageId = 10L,
                chat = TelegramChat(id = 999L),
                text = text
            )
        )
    }

    @Test
    fun `should handle funcao command and send upcoming duty message`() {
        val theme = Theme(UUID.randomUUID(), "Gira de Exu", null, LocalDateTime.now())
        val duty = Duty(
            id = UUID.randomUUID(),
            theme = theme,
            dutyType = DutyTypeEnum.OPENED_GIRA,
            date = LocalDate.now().plusDays(5),
            period = SemesterEnum.FIRST_SEMESTER,
            year = LocalDate.now().year,
            events = mutableSetOf(),
            createdAt = LocalDateTime.now()
        )

        whenever(dutyUsecase.findAll()).thenReturn(listOf(duty))

        handler.handle(createUpdate("/funcao"))

        verify(telegramClient).sendMessage(eq("999"), check { message ->
            assert(message.contains("Gira de Exu"))
        })
    }

    @Test
    fun `should handle cartao_funcao command and send photo`() {
        val theme = Theme(UUID.randomUUID(), "Gira de Cura", null, LocalDateTime.now())
        val dutyId = UUID.randomUUID()
        val duty = Duty(
            id = dutyId,
            theme = theme,
            dutyType = DutyTypeEnum.OPENED_GIRA,
            date = LocalDate.now().plusDays(2),
            period = SemesterEnum.FIRST_SEMESTER,
            year = LocalDate.now().year,
            events = mutableSetOf(),
            createdAt = LocalDateTime.now()
        )

        whenever(dutyUsecase.findAll()).thenReturn(listOf(duty))
        whenever(cardUsecase.generateCard(eq(dutyId), eq(true)))
            .thenReturn(CardReturn.Render(byteArrayOf(1, 2, 3)))

        handler.handle(createUpdate("/cartao_funcao"))

        verify(telegramClient).sendPhoto(eq("999"), any(), eq("cartao-gira.png"), any())
    }
}
