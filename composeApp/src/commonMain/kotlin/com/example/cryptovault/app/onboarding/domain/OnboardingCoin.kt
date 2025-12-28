/**
 * OnboardingCoin.kt
 *
 * Defines the cryptocurrency options available during the onboarding
 * coin selection step. Each coin has visual properties for display
 * in the selection grid.
 *
 * @see CoinSelectionStep for the UI that displays these coins
 * @see CoinSelectionCard for the individual coin card component
 */
package com.example.cryptovault.app.onboarding.domain

import androidx.compose.ui.graphics.Color

/**
 * Data class representing a cryptocurrency option in the onboarding flow.
 *
 * @property symbol The ticker symbol (e.g., "BTC", "ETH")
 * @property name The full name of the cryptocurrency
 * @property icon The emoji or character icon representing the coin
 * @property gradientColors List of colors for the coin's gradient background
 */
data class OnboardingCoin(
    val symbol: String,
    val name: String,
    val icon: String,
    val gradientColors: List<Color>
)

/**
 * List of popular cryptocurrencies displayed during onboarding.
 *
 * These coins are shown in the coin selection step, allowing users
 * to quickly add popular cryptocurrencies to their watchlist.
 * Each coin has unique gradient colors for visual distinction.
 */
val popularCoins = listOf(
    OnboardingCoin(
        symbol = "BTC",
        name = "Bitcoin",
        icon = "₿",
        gradientColors = listOf(
            Color(0xFFF97316), // orange-500
            Color(0xFFEAB308)  // yellow-500
        )
    ),
    OnboardingCoin(
        symbol = "ETH",
        name = "Ethereum",
        icon = "Ξ",
        gradientColors = listOf(
            Color(0xFF3B82F6), // blue-500
            Color(0xFFA855F7)  // purple-500
        )
    ),
    OnboardingCoin(
        symbol = "BNB",
        name = "Binance",
        icon = "B",
        gradientColors = listOf(
            Color(0xFFFACC15), // yellow-400
            Color(0xFFF97316)  // orange-500
        )
    ),
    OnboardingCoin(
        symbol = "SOL",
        name = "Solana",
        icon = "S",
        gradientColors = listOf(
            Color(0xFFA855F7), // purple-500
            Color(0xFFEC4899)  // pink-500
        )
    ),
    OnboardingCoin(
        symbol = "ADA",
        name = "Cardano",
        icon = "A",
        gradientColors = listOf(
            Color(0xFF60A5FA), // blue-400
            Color(0xFF22D3EE)  // cyan-500
        )
    ),
    OnboardingCoin(
        symbol = "XRP",
        name = "Ripple",
        icon = "X",
        gradientColors = listOf(
            Color(0xFF9CA3AF), // gray-400
            Color(0xFF64748B)  // slate-500
        )
    )
)
