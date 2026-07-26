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
    fun `GET cards preview should return HTML string`() {
        whenever(cardUsecasePort.getPreview(anyOrNull()))
            .thenReturn("<html>Preview Card</html>")

        mockMvc.perform(get("/cards/preview"))
            .andExpect(status().isOk)
            .andExpect(content().string("<html>Preview Card</html>"))
    }

    @Test
    fun `GET cards render should return PNG image byte array`() {
        val dummyPng = byteArrayOf(13, 10, 26, 10)
        whenever(cardUsecasePort.renderCardPng(anyOrNull()))
            .thenReturn(dummyPng)

        mockMvc.perform(get("/cards/render"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.IMAGE_PNG))
            .andExpect(content().bytes(dummyPng))
    }

    @Test
    fun `GET cards preview with dutyId parameter should pass dutyId to usecase`() {
        val dutyId = UUID.randomUUID()
        whenever(cardUsecasePort.getPreview(eq(dutyId)))
            .thenReturn("<html>Preview for Duty</html>")

        mockMvc.perform(get("/cards/preview").param("dutyId", dutyId.toString()))
            .andExpect(status().isOk)
            .andExpect(content().string("<html>Preview for Duty</html>"))
    }

    @Test
    fun `GET cards render with dutyId path variable should render PNG for specific duty`() {
        val dutyId = UUID.randomUUID()
        val dummyPng = byteArrayOf(1, 2, 3, 4)
        whenever(cardUsecasePort.renderCardPng(eq(dutyId)))
            .thenReturn(dummyPng)

        mockMvc.perform(get("/cards/render/$dutyId"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.IMAGE_PNG))
            .andExpect(content().bytes(dummyPng))
    }
}
