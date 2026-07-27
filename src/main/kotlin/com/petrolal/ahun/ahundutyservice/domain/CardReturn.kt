package com.petrolal.ahun.ahundutyservice.domain

sealed class CardReturn {
    data class Preview(val html: String) : CardReturn()
    data class Render(val png: ByteArray) : CardReturn()
}
