package com.example.valguard.app.realtime.data

import com.example.valguard.app.realtime.data.dto.PriceUpdateMessageDto
import com.example.valguard.app.realtime.data.dto.SubscriptionRequestDto
import com.example.valguard.app.realtime.data.dto.toJson
import com.example.valguard.app.realtime.data.dto.toPriceUpdateMessageDto
import com.example.valguard.app.realtime.domain.ConnectionState
import com.example.valguard.app.realtime.domain.PriceUpdate
import com.example.valguard.app.realtime.domain.ReconnectionStrategy
import com.example.valguard.app.realtime.domain.WebSocketClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class KtorWebSocketClient(
    private val httpClient: HttpClient,
    private val webSocketUrl: String,
    private val reconnectionStrategy: ReconnectionStrategy
) : WebSocketClient {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _priceUpdates = MutableSharedFlow<PriceUpdate>(extraBufferCapacity = 64)
    override val priceUpdates: Flow<PriceUpdate> = _priceUpdates.asSharedFlow()

    private var session: WebSocketSession? = null
    private var messageJob: Job? = null
    private var reconnectAttempt = 0

    // Track previous prices for direction calculation
    private val previousPrices = mutableMapOf<String, Double>()

    override suspend fun connect() {
        if (_connectionState.value == ConnectionState.CONNECTED ||
            _connectionState.value == ConnectionState.CONNECTING
        ) {
            return
        }

        _connectionState.value = ConnectionState.CONNECTING

        try {
            session = httpClient.webSocketSession(webSocketUrl)
            _connectionState.value = ConnectionState.CONNECTED
            reconnectAttempt = 0
            reconnectionStrategy.reset()
            startMessageListener()
        } catch (e: Exception) {
            handleConnectionFailure()
        }
    }

    override suspend fun disconnect() {
        messageJob?.cancel()
        messageJob = null

        try {
            session?.close()
        } catch (e: Exception) {
            // Ignore close errors
        }

        session = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override suspend fun subscribe(coinIds: List<String>) {
        if (coinIds.isEmpty()) return

        val request = SubscriptionRequestDto(
            action = SubscriptionRequestDto.ACTION_SUBSCRIBE,
            coinIds = coinIds
        )
        sendMessage(request.toJson())
    }

    override suspend fun unsubscribe(coinIds: List<String>) {
        if (coinIds.isEmpty()) return

        val request = SubscriptionRequestDto(
            action = SubscriptionRequestDto.ACTION_UNSUBSCRIBE,
            coinIds = coinIds
        )
        sendMessage(request.toJson())
    }

    private suspend fun sendMessage(message: String) {
        try {
            session?.send(Frame.Text(message))
        } catch (e: Exception) {
            // Handle send failure - connection might be lost
            handleConnectionFailure()
        }
    }

    private fun startMessageListener() {
        messageJob?.cancel()
        messageJob = scope.launch {
            val currentSession = session ?: return@launch

            try {
                for (frame in currentSession.incoming) {
                    if (!isActive) break

                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            parseAndEmitPriceUpdate(text)
                        }
                        else -> { /* Ignore other frame types */ }
                    }
                }
            } catch (e: Exception) {
                // Connection lost
                handleConnectionFailure()
            }
        }
    }

    private suspend fun parseAndEmitPriceUpdate(text: String) {
        try {
            val dto = text.toPriceUpdateMessageDto()
            val currentPrice = dto.price.toDoubleOrNull() ?: return
            val previousPrice = previousPrices[dto.coinId] ?: currentPrice

            val priceUpdate = PriceUpdate(
                coinId = dto.coinId,
                price = currentPrice,
                previousPrice = previousPrice,
                timestamp = dto.timestamp
            )

            // Update previous price for next comparison
            previousPrices[dto.coinId] = currentPrice

            _priceUpdates.emit(priceUpdate)
        } catch (e: Exception) {
            // Log parse error, skip malformed message
        }
    }

    private suspend fun handleConnectionFailure() {
        session = null
        messageJob?.cancel()
        messageJob = null

        if (reconnectionStrategy.shouldFallback(reconnectAttempt)) {
            _connectionState.value = ConnectionState.FAILED
            return
        }

        _connectionState.value = ConnectionState.RECONNECTING
        val delayMs = reconnectionStrategy.nextDelay(reconnectAttempt)
        reconnectAttempt++

        delay(delayMs)
        connect()
    }
}
