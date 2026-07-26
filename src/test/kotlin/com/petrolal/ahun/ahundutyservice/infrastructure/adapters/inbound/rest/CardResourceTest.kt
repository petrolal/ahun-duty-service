package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.inbound.rest

import com.petrolal.ahun.ahundutyservice.application.ports.CardUsecasePort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

class CardResourceTest {

    private lateinit var cardUsecasePort: CardUsecasePort
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        cardUsecasePort = mock()
        val cardResource = CardResource(cardUsecasePort)
        mockMvc = MockMvcBuilders.standaloneSetup(cardResource).build()
    }

    @Test
    fun `GET cards preview with dutyId path variable should return HTML string`() {
        val dutyId = UUID.randomUUID()
        whenever(cardUsecasePort.getPreview(eq(dutyId)))
            .thenReturn("<html>Preview for Duty</html>")

        mockMvc.perform(get("/cards/$dutyId/preview"))
            .andExpect(status().isOk)
            .andExpect(content().string("<html>Preview for Duty</html>"))
    }

    @Test
    fun `GET cards render with dutyId path variable should render and download PNG`() {
        val dutyId = UUID.randomUUID()
        val dummyPng = byteArrayOf(1, 2, 3, 4)
        whenever(cardUsecasePort.renderCardPng(eq(dutyId)))
            .thenReturn(dummyPng)

        mockMvc.perform(get("/cards/$dutyId/render"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.IMAGE_PNG))
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"card-$dutyId.png\""))
            .andExpect(content().bytes(dummyPng))
    }
}
