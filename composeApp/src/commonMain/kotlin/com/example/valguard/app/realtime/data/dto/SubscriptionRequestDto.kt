package com.example.valguard.app.realtime.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// DTO for subscription/unsubscription requests sent to WebSocket.

@Serializable
data class SubscriptionRequestDto(
    val action: String,
    val coinIds: List<String>
) {
    companion object {
        const val ACTION_SUBSCRIBE = "subscribe"
        const val ACTION_UNSUBSCRIBE = "unsubscribe"
    }
}


fun SubscriptionRequestDto.toJson(): String {
    return Json.encodeToString(this)
}


fun String.toSubscriptionRequestDto(): SubscriptionRequestDto {
    return Json.decodeFromString(this)
}
