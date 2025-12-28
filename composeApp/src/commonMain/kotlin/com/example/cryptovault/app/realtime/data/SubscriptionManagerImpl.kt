package com.example.cryptovault.app.realtime.data

import com.example.cryptovault.app.realtime.domain.SubscriptionManager

class SubscriptionManagerImpl : SubscriptionManager {

    private val subscriptions = mutableMapOf<String, Set<String>>()
    private val lock = Any()

    override fun subscribe(screenId: String, coinIds: List<String>) {
        synchronized(lock) {
            subscriptions[screenId] = coinIds.toSet()
        }
    }

    override fun unsubscribe(screenId: String) {
        synchronized(lock) {
            subscriptions.remove(screenId)
        }
    }

    override fun getActiveSubscriptions(): Set<String> {
        synchronized(lock) {
            return subscriptions.values.flatten().toSet()
        }
    }

    override fun getSubscriptionsForScreen(screenId: String): Set<String> {
        synchronized(lock) {
            return subscriptions[screenId] ?: emptySet()
        }
    }
}
