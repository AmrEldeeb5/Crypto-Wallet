package com.example.cryptovault.app.realtime.data

import com.example.cryptovault.app.realtime.domain.ConnectionState
import com.example.cryptovault.app.realtime.domain.PriceUpdate
import com.example.cryptovault.app.realtime.domain.WebSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeWebSocketClient : WebSocketClient {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _priceUpdates = MutableSharedFlow<PriceUpdate>(extraBufferCapacity = 64)
    override val priceUpdates: Flow<PriceUpdate> = _priceUpdates.asSharedFlow()

    val subscribedCoinIds = mutableSetOf<String>()
    var connectCalled = false
    var disconnectCalled = false

    override suspend fun connect() {
        connectCalled = true
        _connectionState.value = ConnectionState.CONNECTED
    }

    override suspend fun disconnect() {
        disconnectCalled = true
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override suspend fun subscribe(coinIds: List<String>) {
        subscribedCoinIds.addAll(coinIds)
    }

    override suspend fun unsubscribe(coinIds: List<String>) {
        subscribedCoinIds.removeAll(coinIds.toSet())
    }

    // Test helpers

    fun setConnectionState(state: ConnectionState) {
        _connectionState.value = state
    }

    suspend fun emitPriceUpdate(priceUpdate: PriceUpdate) {
        _priceUpdates.emit(priceUpdate)
    }

    fun reset() {
        _connectionState.value = ConnectionState.DISCONNECTED
        subscribedCoinIds.clear()
        connectCalled = false
        disconnectCalled = false
    }
}
