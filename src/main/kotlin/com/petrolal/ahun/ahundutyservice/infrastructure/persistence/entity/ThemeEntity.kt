package com.petrolal.ahun.ahundutyservice.infrastructure.persistence.entity

import com.petrolal.ahun.ahundutyservice.domain.Theme
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "theme")
class ThemeEntity(
    @Id
    var id: UUID,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var description: String?,

    @Column(nullable = false)
    var createdAt: LocalDateTime,

    @Column(nullable = true)
    var updatedAt: LocalDateTime?,

) {
    companion object {
        fun toEntity(theme: Theme): ThemeEntity =
            ThemeEntity(
                id = theme.id,
                name = theme.name,
                description = theme.description,
                createdAt = theme.createdAt,
                updatedAt = theme.updatedAt,
            )

        fun toDomain(themeEntity: ThemeEntity): Theme =
            Theme(
                id = themeEntity.id,
                name = themeEntity.name,
                description = themeEntity.description,
                createdAt = themeEntity.createdAt,
                updatedAt = themeEntity.updatedAt,
            )
    }
}
