package com.example.valguard.app.realtime.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter


class ObservePriceUpdatesUseCase(
    private val priceRepository: PriceRepository,
    private val subscriptionManager: SubscriptionManager
) {

    val connectionState: StateFlow<ConnectionState>
        get() = priceRepository.connectionState


    val allPriceUpdates: Flow<PriceUpdate>
        get() = priceRepository.priceUpdates


    suspend fun start() {
        priceRepository.connect()
    }


    suspend fun stop() {
        priceRepository.disconnect()
    }


    suspend fun subscribeScreen(screenId: String, coinIds: List<String>) {
        val previousSubscriptions = subscriptionManager.getActiveSubscriptions()
        subscriptionManager.subscribe(screenId, coinIds)
        val newSubscriptions = subscriptionManager.getActiveSubscriptions()

        // Subscribe to new coins that weren't previously subscribed
        val coinsToSubscribe = newSubscriptions - previousSubscriptions
        if (coinsToSubscribe.isNotEmpty()) {
            priceRepository.subscribe(coinsToSubscribe.toList())
        }
    }


    suspend fun unsubscribeScreen(screenId: String) {
        val previousSubscriptions = subscriptionManager.getActiveSubscriptions()
        subscriptionManager.unsubscribe(screenId)
        val remainingSubscriptions = subscriptionManager.getActiveSubscriptions()

        // Unsubscribe from coins that are no longer needed
        val coinsToUnsubscribe = previousSubscriptions - remainingSubscriptions
        if (coinsToUnsubscribe.isNotEmpty()) {
            priceRepository.unsubscribe(coinsToUnsubscribe.toList())
        }
    }


    fun priceUpdatesFor(coinIds: List<String>): Flow<PriceUpdate> {
        val coinIdSet = coinIds.toSet()
        return priceRepository.priceUpdates.filter { it.coinId in coinIdSet }
    }


    fun priceUpdatesFor(coinId: String): Flow<PriceUpdate> {
        return priceRepository.priceUpdates.filter { it.coinId == coinId }
    }
}
