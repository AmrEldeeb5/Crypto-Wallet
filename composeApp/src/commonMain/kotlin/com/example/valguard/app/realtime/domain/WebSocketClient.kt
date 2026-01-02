package com.example.valguard.app.realtime.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WebSocketClient {

    val connectionState: StateFlow<ConnectionState>


    val priceUpdates: Flow<PriceUpdate>


    suspend fun connect()


    suspend fun disconnect()


    suspend fun subscribe(coinIds: List<String>)


    suspend fun unsubscribe(coinIds: List<String>)
}
