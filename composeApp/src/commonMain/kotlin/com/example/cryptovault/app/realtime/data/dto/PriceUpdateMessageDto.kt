package com.example.cryptovault.app.realtime.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


 // DTO for price update messages received from WebSocket.

@Serializable
data class PriceUpdateMessageDto(
    val type: String,
    val coinId: String,
    val price: String,
    val timestamp: Long
)


fun PriceUpdateMessageDto.toJson(): String {
    return Json.encodeToString(this)
}


fun String.toPriceUpdateMessageDto(): PriceUpdateMessageDto {
    return Json.decodeFromString(this)
}
