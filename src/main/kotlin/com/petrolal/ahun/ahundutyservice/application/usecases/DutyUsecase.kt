package com.petrolal.ahun.ahundutyservice.application.usecases

import com.petrolal.ahun.ahundutyservice.application.ports.DutyEventRepositoryPort
import com.petrolal.ahun.ahundutyservice.application.ports.DutyRepositoryPort
import com.petrolal.ahun.ahundutyservice.application.ports.ThemeRepositoryPort
import com.petrolal.ahun.ahundutyservice.domain.Duty
import com.petrolal.ahun.ahundutyservice.domain.DutyEvent
import com.petrolal.ahun.ahundutyservice.domain.DutyTypeEnum
import com.petrolal.ahun.ahundutyservice.domain.SemesterEnum
import com.petrolal.ahun.ahundutyservice.domain.dto.DutyRequestDto
import com.petrolal.ahun.ahundutyservice.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

/**
 * Application service orchestrating business use cases for [Duty]s.
 * Direct inbound entry point for Web REST controllers.
 */
@Service
@Transactional(readOnly = true)
class DutyUsecase(
    private val repository: DutyRepositoryPort,
    private val repositoryTheme: ThemeRepositoryPort,
    private val repositoryDutyEvent: DutyEventRepositoryPort
) {

    fun findByThemeName(themeName: String): List<Duty> = repository.findByThemeName(themeName)

    fun findByDutyType(dutyType: DutyTypeEnum): List<Duty> = repository.findByDutyType(dutyType)

    fun findByThemeNameAndDutyType(
        themeName: String,
        dutyType: DutyTypeEnum,
    ): List<Duty> = repository.findByThemeNameAndDutyType(themeName, dutyType)

    fun findAll(): List<Duty> = repository.findAll()

    fun findById(id: UUID): Duty =
        repository.findById(id)
            ?: throw ResourceNotFoundException("Duty with id $id not found")

    @Transactional
    fun create(requestDto: DutyRequestDto): Duty {
        val theme = repositoryTheme.findById(requestDto.themeId)
            ?: throw ResourceNotFoundException("Theme with id ${requestDto.themeId} not found")

        val allEvents = resolveEvents(requestDto)

        val calculatedPeriod = requestDto.period ?: SemesterEnum.from(requestDto.date)
        val calculatedYear = requestDto.date.year

        val duty = Duty(
            id = UUID.randomUUID(),
            theme = theme,
            dutyType = requestDto.dutyType,
            date = requestDto.date,
            period = calculatedPeriod,
            description = requestDto.description,
            year = calculatedYear,
            events = allEvents.toMutableSet(),
            createdAt = LocalDateTime.now(),
            updatedAt = null,
        )

        return repository.create(duty)
    }

    @Transactional
    fun update(
        id: UUID,
        requestDto: DutyRequestDto,
    ): Duty {
        val existingDuty = repository.findById(id)
            ?: throw ResourceNotFoundException("Duty with id $id not found")

        val theme = repositoryTheme.findById(requestDto.themeId)
            ?: throw ResourceNotFoundException("Theme with id ${requestDto.themeId} not found")

        val allEvents = resolveEvents(requestDto)

        val calculatedPeriod = requestDto.period ?: SemesterEnum.from(requestDto.date)
        val calculatedYear = requestDto.date.year

        val updatedDuty = existingDuty.copy(
            theme = theme,
            dutyType = requestDto.dutyType,
            date = requestDto.date,
            period = calculatedPeriod,
            description = requestDto.description,
            year = calculatedYear,
            events = allEvents.toMutableSet(),
            updatedAt = LocalDateTime.now()
        )

        return repository.update(id, updatedDuty)
    }

    private fun resolveEvents(requestDto: DutyRequestDto): List<DutyEvent> {
        val existingEvents = if (requestDto.eventIds.isNotEmpty()) {
            val fetched = repositoryDutyEvent.findAllById(requestDto.eventIds)
            if (fetched.size != requestDto.eventIds.size) {
                throw ResourceNotFoundException("One or more events not found")
            }
            fetched
        } else emptyList()

        val createdInlineEvents = if (!requestDto.inlineEvents.isNullOrEmpty()) {
            val domainInline = requestDto.inlineEvents.map {
                DutyEvent(
                    id = UUID.randomUUID(),
                    name = it.name,
                    startedAt = it.startedAt,
                    visibleInCard = it.visibleInCard,
                    description = it.description,
                    createdAt = LocalDateTime.now(),
                    updatedAt = null
                )
            }
            repositoryDutyEvent.create(domainInline)
        } else emptyList()

        return existingEvents + createdInlineEvents
    }
}
