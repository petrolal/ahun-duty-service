package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest.assembler

import com.petrolal.ahun.ahundutyservice.domain.Duty
import com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest.CardResource
import com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest.DutyResource
import com.petrolal.commons.web.hateoas.BaseModelAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Component

/**
 * HATEOAS representation model assembler for [Duty] domain entities.
 * Generates HAL compliant _links pointing to duty self details, card rendering, and card preview endpoints.
 */
@Component
class DutyModelAssembler : BaseModelAssembler<Duty, Duty>() {

    override fun toResourceDto(entity: Duty): Duty = entity

    override fun addLinks(entity: Duty, model: EntityModel<Duty>) {
        val selfLink = linkTo(DutyResource::class.java)
            .slash(entity.id)
            .withSelfRel()

        val renderLink = linkTo(CardResource::class.java)
            .slash(entity.id)
            .slash("render")
            .withRel("card-render")
            .withType("image/png")

        val previewLink = linkTo(CardResource::class.java)
            .slash(entity.id)
            .slash("preview")
            .withRel("card-preview")
            .withType("text/html")

        val allDutiesLink = linkTo(DutyResource::class.java)
            .withRel("all-duties")

        model.add(selfLink, renderLink, previewLink, allDutiesLink)
    }
}

