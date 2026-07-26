package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.outbound.rendering

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class ThymeleafCardRendererAdapterTest {

    private val templateEngine = SpringTemplateEngine().apply {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            templateMode = TemplateMode.HTML
            characterEncoding = "UTF-8"
        })
    }

    private val cardRendererAdapter = ThymeleafCardRendererAdapter(templateEngine)

    @Test
    fun `renderPng should output an image with 1080x1350 resolution`() {
        val resource = org.springframework.core.io.ClassPathResource("static/images/gira_de_exu_e_cura_2.png")
        val bgBase64 = if (resource.exists()) {
            "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(resource.inputStream.readAllBytes())
        } else ""

        val variables = mapOf(
            "events" to listOf(mapOf("name" to "GIRA DE TESTE", "time" to "18H", "visibleInCard" to true)),
            "date" to "20/05/2026",
            "bgImageName" to "gira_de_exu_e_cura_2.png",
            "bgImageDataUri" to bgBase64
        )

        val imageBytes = cardRendererAdapter.renderPng("2_fields_template", variables)
        assertNotNull(imageBytes)

        val bufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
        assertNotNull(bufferedImage)
        assertEquals(1080, bufferedImage.width)
        assertEquals(1350, bufferedImage.height)
        
        val centerPixel = bufferedImage.getRGB(540, 675)
        // Verify background image is loaded and non-transparent/non-white
        org.junit.jupiter.api.Assertions.assertNotEquals(-1, centerPixel)
    }
}
