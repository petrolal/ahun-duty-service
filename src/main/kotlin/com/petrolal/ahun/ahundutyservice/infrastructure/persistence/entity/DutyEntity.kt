package com.petrolal.ahun.ahundutyservice.infrastructure.persistence.entity

import com.petrolal.ahun.ahundutyservice.domain.Duty
import com.petrolal.ahun.ahundutyservice.domain.DutyTypeEnum
import com.petrolal.ahun.ahundutyservice.domain.SemesterEnum
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "duties")
class DutyEntity(
    @Id
    var id: UUID,

    @Column(nullable = false)
    var date: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    var theme: ThemeEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var dutyType: DutyTypeEnum,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var period: SemesterEnum,

    @Column(nullable = true)
    var description: String?,

    @Column(nullable = false)
    var year: Int,

    @ManyToMany
    @JoinTable(
        name = "duties_events",
        joinColumns = [JoinColumn(name = "duty_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "event_id", referencedColumnName = "id")]
    )
    var events: MutableSet<DutyEventEntity>,

    @Column(nullable = false)
    var createdAt: LocalDateTime,

    @Column(nullable = true)
    var updatedAt: LocalDateTime?,

) {
    companion object {
        fun toDomain(dutyEntity: DutyEntity): Duty =
            Duty(
                id = dutyEntity.id,
                theme = ThemeEntity.toDomain(dutyEntity.theme),
                dutyType = dutyEntity.dutyType,
                date = dutyEntity.date,
                period = dutyEntity.period,
                description = dutyEntity.description,
                year = dutyEntity.year,
                events = dutyEntity.events.map { DutyEventEntity.toDomain(it) }.toMutableSet(),
                createdAt = dutyEntity.createdAt,
                updatedAt = dutyEntity.updatedAt,
            )

        fun toEntity(duty: Duty): DutyEntity =
            DutyEntity(
                id = duty.id,
                date = duty.date,
                dutyType = duty.dutyType,
                theme = ThemeEntity(
                    duty.theme.id,
                    duty.theme.name,
                    duty.theme.description,
                    duty.theme.createdAt,
                    duty.theme.updatedAt,
                ),
                period = duty.period,
                description = duty.description,
                year = duty.year,
                events = duty.events.map { DutyEventEntity.toEntity(it) }.toMutableSet(),
                createdAt = duty.createdAt,
                updatedAt = duty.updatedAt,
            )
    }
}
