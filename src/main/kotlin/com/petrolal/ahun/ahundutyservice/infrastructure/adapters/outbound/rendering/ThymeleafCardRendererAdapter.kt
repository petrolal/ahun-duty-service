package com.petrolal.ahun.ahundutyservice.infrastructure.adapters.outbound.rendering

import com.lowagie.text.Image
import com.petrolal.ahun.ahundutyservice.application.ports.CardRenderPort
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.xhtmlrenderer.pdf.ITextFSImage
import org.xhtmlrenderer.pdf.ITextOutputDevice
import org.xhtmlrenderer.pdf.ITextRenderer
import org.xhtmlrenderer.pdf.ITextUserAgent
import org.xhtmlrenderer.resource.ImageResource
import java.awt.Image as AwtImage
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

/**
 * Custom UserAgentCallback that supports base64 data URIs (`data:image/...`) in FlyingSaucer PDF rendering.
 */
class DataUriITextUserAgent(outputDevice: ITextOutputDevice) : ITextUserAgent(outputDevice) {
    override fun getImageResource(uri: String?): ImageResource? {
        val cleanUri = uri?.trim('\'', '"')
        if (cleanUri != null && cleanUri.startsWith("data:image/")) {
            try {
                val base64Data = cleanUri.substringAfter("base64,")
                val imageBytes = Base64.getDecoder().decode(base64Data)
                val image = Image.getInstance(imageBytes)
                if (image != null) {
                    val fsImage = ITextFSImage(image)
                    return ImageResource(cleanUri, fsImage)
                }
            } catch (_: Exception) {
                // fallback to default loader if decoding fails
            }
        }
        return super.getImageResource(uri)
    }
}

/**
 * Outbound adapter implementing [CardRenderPort] using Thymeleaf, FlyingSaucer (ITextRenderer), and PDFBox.
 */
@Component
class ThymeleafCardRendererAdapter(
    private val templateEngine: TemplateEngine
) : CardRenderPort {

    override fun renderHtml(templateName: String, variables: Map<String, Any>): String {
        val context = Context().apply {
            variables.forEach { (key, value) -> setVariable(key, value) }
        }
        return templateEngine.process(templateName, context)
    }

    override fun renderPng(templateName: String, variables: Map<String, Any>): ByteArray {
        val htmlContent = renderHtml(templateName, variables)

        // 1. Generate PDF using FlyingSaucer ITextRenderer
        val pdfOutputStream = ByteArrayOutputStream()
        val renderer = ITextRenderer()

        val userAgent = DataUriITextUserAgent(renderer.outputDevice)
        userAgent.sharedContext = renderer.sharedContext
        renderer.sharedContext.userAgentCallback = userAgent

        renderer.setDocumentFromString(htmlContent)
        renderer.layout()
        renderer.createPDF(pdfOutputStream)

        val pdfBytes = pdfOutputStream.toByteArray()

        // 2. Convert PDF to PNG image using PDFBox
        Loader.loadPDF(pdfBytes).use { document ->
            val pdfRenderer = PDFRenderer(document)
            val renderDpi = 300f
            val image = pdfRenderer.renderImageWithDPI(0, renderDpi)

            val finalImage = if (image.width != 1080 || image.height != 1350) {
                val resized = BufferedImage(1080, 1350, BufferedImage.TYPE_INT_ARGB)
                val g2d = resized.createGraphics()
                g2d.drawImage(image.getScaledInstance(1080, 1350, AwtImage.SCALE_SMOOTH), 0, 0, null)
                g2d.dispose()
                resized
            } else {
                image
            }

            val pngOutputStream = ByteArrayOutputStream()
            ImageIO.write(finalImage, "png", pngOutputStream)
            return pngOutputStream.toByteArray()
        }
    }
}

