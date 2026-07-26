package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest

import com.petrolal.ahun.ahundutyservice.application.usecases.DutyUsecase
import com.petrolal.ahun.ahundutyservice.domain.*
import com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest.assembler.DutyModelAssembler
import org.hamcrest.Matchers.endsWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DutyResourceTest {

    private lateinit var dutyUsecase: DutyUsecase
    private lateinit var dutyModelAssembler: DutyModelAssembler
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        dutyUsecase = mock(DutyUsecase::class.java)
        dutyModelAssembler = DutyModelAssembler()
        val dutyResource = DutyResource(dutyUsecase, dutyModelAssembler)

        mockMvc = MockMvcBuilders.standaloneSetup(dutyResource).build()
    }

    @Test
    fun `GET duty by id should return HATEOAS entity model with hypermedia links`() {
        val dutyId = UUID.randomUUID()
        val theme = Theme(UUID.randomUUID(), "Gira de Exu e Cura", null, LocalDateTime.now())
        val duty = Duty(
            id = dutyId,
            theme = theme,
            dutyType = DutyTypeEnum.OPENED_GIRA,
            date = LocalDate.of(2026, 5, 20),
            period = SemesterEnum.FIRST_SEMESTER,
            year = 2026,
            events = mutableSetOf(),
            createdAt = LocalDateTime.now()
        )

        `when`(dutyUsecase.findById(dutyId)).thenReturn(duty)

        mockMvc.perform(get("/duties/$dutyId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(dutyId.toString()))
            .andExpect(jsonPath("$.links[0].rel").value("self"))
            .andExpect(jsonPath("$.links[0].href").value(endsWith("/duties/$dutyId")))
            .andExpect(jsonPath("$.links[1].rel").value("card-render"))
            .andExpect(jsonPath("$.links[1].href").value(endsWith("/cards/$dutyId/render")))
            .andExpect(jsonPath("$.links[2].rel").value("card-preview"))
            .andExpect(jsonPath("$.links[2].href").value(endsWith("/cards/$dutyId/preview")))
            .andExpect(jsonPath("$.links[3].rel").value("all-duties"))
            .andExpect(jsonPath("$.links[3].href").value(endsWith("/duties")))
    }
}
