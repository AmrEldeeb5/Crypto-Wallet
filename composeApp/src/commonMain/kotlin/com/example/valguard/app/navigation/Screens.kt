package com.example.valguard.app.navigation

import kotlinx.serialization.Serializable

sealed interface Screens {

    @Serializable
    data object Splash : Screens

    @Serializable
    data object Onboarding : Screens

    @Serializable
    data object Main : Screens

    @Serializable
    data object Coins : Screens

    @Serializable
    data object Portfolio : Screens

    @Serializable
    data class Sell(val coinId: String) : Screens

    @Serializable
    data class Buy(val coinId: String) : Screens
    
    @Serializable
    data class CoinDetail(val coinId: String) : Screens
    
    @Serializable
    data object DCA : Screens
    
    @Serializable
    data object Compare : Screens
    
    @Serializable
    data object Referral : Screens
}