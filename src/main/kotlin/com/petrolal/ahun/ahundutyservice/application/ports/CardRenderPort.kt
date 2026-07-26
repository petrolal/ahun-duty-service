package com.petrolal.ahun.ahundutyservice.application.ports

/**
 * Outbound port interface for rendering card templates into HTML or PNG assets.
 */
interface CardRenderPort {
    /**
     * Renders an HTML template string populated with the provided model variables.
     */
    fun renderHtml(
        templateName: String,
        variables: Map<String, Any>,
    ): String

    /**
     * Renders a PNG image byte array from the given HTML template and model variables.
     */
    fun renderPng(
        templateName: String,
        variables: Map<String, Any>,
    ): ByteArray
}
