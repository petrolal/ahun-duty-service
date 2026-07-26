package com.petrolal.ahun.ahundutyservice.application.usecases

import com.petrolal.ahun.ahundutyservice.application.ports.CardRenderPort
import com.petrolal.ahun.ahundutyservice.application.ports.CardUsecasePort
import com.petrolal.ahun.ahundutyservice.application.ports.DutyRepositoryPort
import com.petrolal.ahun.ahundutyservice.application.ports.FileStoragePort
import com.petrolal.ahun.ahundutyservice.application.ports.TemplateRepositoryPort
import com.petrolal.ahun.ahundutyservice.domain.Duty
import com.petrolal.ahun.ahundutyservice.domain.DutyTypeEnum
import com.petrolal.ahun.ahundutyservice.domain.Theme
import com.petrolal.ahun.ahundutyservice.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Locale
import java.util.UUID

/**
 * Application service orchestrating Card generation business logic.
 * Implements [CardUsecasePort] following Hexagonal Architecture.
 */
@Service
@Transactional(readOnly = true)
class CardUsecase(
    private val dutyRepository: DutyRepositoryPort,
    private val cardRenderPort: CardRenderPort,
    private val templateRepository: TemplateRepositoryPort,
    private val fileStoragePort: FileStoragePort,
) : CardUsecasePort {

    override fun getPreview(dutyId: UUID?): String {
        val cardData = resolveCardData(dutyId)
        val formattedDate = cardData.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val variables =
            mapOf(
                "dutyId" to (dutyId?.toString() ?: ""),
                "events" to cardData.events,
                "bgImageName" to cardData.bgImageName,
                "bgImageDataUri" to cardData.bgImageDataUri,
                "date" to formattedDate,
            )
        return cardRenderPort.renderHtml("preview_card_template", variables)
    }

    override fun renderCardPng(dutyId: UUID?): ByteArray {
        val cardData = resolveCardData(dutyId)
        val formattedDate = cardData.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val variables =
            mapOf(
                "dutyId" to (dutyId?.toString() ?: ""),
                "events" to cardData.events,
                "bgImageName" to cardData.bgImageName,
                "bgImageDataUri" to cardData.bgImageDataUri,
                "date" to formattedDate,
            )
        return cardRenderPort.renderPng("2_fields_template", variables)
    }

    data class CardEventData(
        val name: String,
        val time: String,
        val description: String? = null,
        val visibleInCard: Boolean = true,
    )

    private data class CardData(
        val events: List<CardEventData>,
        val bgImageName: String,
        val bgImageDataUri: String,
        val date: LocalDate,
    )

    private fun resolveCardData(dutyId: UUID?): CardData {
        val duty =
            if (dutyId != null) {
                dutyRepository.findById(dutyId)
                    ?: throw ResourceNotFoundException("Duty with id $dutyId not found")
            } else {
                val now = LocalDate.now()
                dutyRepository.findCurrentMonthDutyByType(
                    dutyType = DutyTypeEnum.OPENED_GIRA,
                    year = now.year,
                    month = now.monthValue,
                ) ?: throw ResourceNotFoundException("No GIRA_ABERTA duty found in database for actual month or latest records")
            }

        val date = duty.date

        val eventsData =
            duty.events
                .sortedBy { it.startedAt }
                .map { event ->
                    val hour = event.startedAt.hour
                    val minute = event.startedAt.minute
                    val timeStr = if (minute == 0) "${hour}H" else "${hour}H${minute.toString().padStart(2, '0')}"
                    CardEventData(
                        name = event.name,
                        time = timeStr,
                        description = event.description,
                        visibleInCard = event.visibleInCard,
                    )
                }

        val bgImageName = resolveBgImageForTheme(duty.theme)
        val bgImageDataUri = loadBase64Image(bgImageName)

        return CardData(eventsData, bgImageName, bgImageDataUri, date)
    }

    private fun resolveBgImageForTheme(theme: Theme): String {
        // 1. Query database for templates bound to this theme ID
        val dbTemplates = templateRepository.findByThemeId(theme.id)
        if (dbTemplates.isNotEmpty()) {
            return dbTemplates.random().imagePath
        }

        // 2. Query database for all templates and match by normalized name
        val allTemplates = templateRepository.findAll()
        val normalizedThemeName = normalizeKey(theme.name)

        val matchedTemplate = allTemplates.firstOrNull { template ->
            val cleanPath = template.imagePath.removeSuffix(".png").replace(Regex("_\\d+$"), "")
            cleanPath == normalizedThemeName || normalizedThemeName.contains(cleanPath) || cleanPath.contains(normalizedThemeName)
        }

        if (matchedTemplate != null) {
            return matchedTemplate.imagePath
        }

        // 3. Global fallback template if database has templates
        if (allTemplates.isNotEmpty()) {
            return allTemplates.first().imagePath
        }

        // 4. Default static fallback image name
        return "gira_de_exu_e_cura_2.png"
    }

    private fun normalizeKey(input: String): String =
        Normalizer
            .normalize(input, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')

    private fun loadBase64Image(imageName: String): String {
        val bytes = fileStoragePort.loadImageAsBytes(imageName)
        val base64 = Base64.getEncoder().encodeToString(bytes)
        return "data:image/png;base64,$base64"
    }

    private fun formatSubtitle(duty: Duty): String {
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE | d 'DE' MMMM", Locale.of("pt", "BR"))
        val dateStr = duty.date.format(dateFormatter).uppercase()

        val visibleEvent =
            duty.events.filter { it.visibleInCard }.minByOrNull { it.startedAt }
                ?: duty.events.minByOrNull { it.startedAt }

        return if (visibleEvent != null) {
            val hour = visibleEvent.startedAt.hour
            val minute = visibleEvent.startedAt.minute
            val timeStr = if (minute == 0) "${hour}H" else "${hour}H${minute.toString().padStart(2, '0')}"
            "$dateStr - $timeStr"
        } else {
            dateStr
        }
    }
}
