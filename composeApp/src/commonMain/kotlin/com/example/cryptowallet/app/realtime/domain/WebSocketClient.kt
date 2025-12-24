package com.example.cryptowallet.app.realtime.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for WebSocket client that handles real-time price updates.
 */
interface WebSocketClient {

    val connectionState: StateFlow<ConnectionState>


    val priceUpdates: Flow<PriceUpdate>


    suspend fun connect()


    suspend fun disconnect()


    suspend fun subscribe(coinIds: List<String>)


    suspend fun unsubscribe(coinIds: List<String>)
}
