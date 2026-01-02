package com.example.valguard.app.realtime.domain

interface SubscriptionManager {

    fun subscribe(screenId: String, coinIds: List<String>)


    fun unsubscribe(screenId: String)


    fun getActiveSubscriptions(): Set<String>


    fun getSubscriptionsForScreen(screenId: String): Set<String>
}
