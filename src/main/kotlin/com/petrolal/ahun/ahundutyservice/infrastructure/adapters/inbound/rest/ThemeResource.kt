package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest

import com.petrolal.ahun.ahundutyservice.application.usecases.ThemeUsecase
import com.petrolal.ahun.ahundutyservice.domain.Theme
import com.petrolal.ahun.ahundutyservice.domain.dto.ThemeRequestDto
import com.petrolal.ahun.ahundutyservice.domain.exception.BadRequestException
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Inbound REST controller for managing Themes.
 * Exposes endpoints for listing, finding, creating, and updating themes.
 */
@Tag(name = "Theme", description = "Theme for the selected Duty and card")
@RestController
@RequestMapping("theme")
class ThemeResource(
    private val themeUsecase: ThemeUsecase,
) {

    /**
     * Endpoint to list all themes, optionally filtered by a name query parameter.
     */
    @GetMapping
    fun list(
        @RequestParam(name = "name", required = false) name: String?,
    ): List<Theme> {
        if (!name.isNullOrEmpty()) {
            return themeUsecase.filterByName(name)
        }
        return themeUsecase.findAll()
    }

    /**
     * Endpoint to get a specific theme by ID.
     */
    @GetMapping("/{id}")
    fun findById(
        @PathVariable("id") id: UUID,
    ): Theme = themeUsecase.findById(id)

    /**
     * Endpoint to create a new theme.
     */
    @PostMapping
    fun create(
        @RequestBody themeRequestDto: ThemeRequestDto,
    ): Theme = themeUsecase.create(themeRequestDto)

    /**
     * Endpoint to update an existing theme.
     * Accepts ID as either path variable (`/theme/{id}`) or query parameter (`/theme?id=...`).
     */
    @PutMapping(value = ["/{id}"])
    fun update(
        @PathVariable(name = "id", required = false) id: UUID,
        @RequestBody themeRequestDto: ThemeRequestDto,
    ): Theme = themeUsecase.update(id, themeRequestDto)
}
