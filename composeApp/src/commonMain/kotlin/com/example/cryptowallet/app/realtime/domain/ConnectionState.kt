package com.example.cryptowallet.app.realtime.domain

/**
 * Represents the current state of the WebSocket connection.
 */
enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    FAILED
}
