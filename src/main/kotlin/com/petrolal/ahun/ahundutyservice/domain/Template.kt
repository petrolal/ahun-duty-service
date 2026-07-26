package com.petrolal.ahun.ahundutyservice.domain

import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain entity representing a card background template associated with a [Theme].
 *
 * @property id Unique identifier of the template.
 * @property name Descriptive name of the template.
 * @property imagePath Filename or relative path of the image asset.
 * @property theme Associated [Theme] domain model.
 * @property createdAt Timestamp when the template record was created.
 */
data class Template(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val imagePath: String,
    val theme: Theme? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
