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
package com.example.valguard.app.onboarding.domain

import androidx.compose.ui.graphics.Color
import valguard.composeapp.generated.resources.Res
import valguard.composeapp.generated.resources.token__binance
import valguard.composeapp.generated.resources.token__bitcoin
import valguard.composeapp.generated.resources.token__cardano
import valguard.composeapp.generated.resources.token__ethereum_classic
import valguard.composeapp.generated.resources.token__solana
import valguard.composeapp.generated.resources.token__xrp
import org.jetbrains.compose.resources.DrawableResource

/**
 * Data class representing a cryptocurrency option in the onboarding flow.
 *
 * @property symbol The ticker symbol (e.g., "BTC", "ETH")
 * @property name The full name of the cryptocurrency
 * @property iconRes The drawable resource for the coin icon
 * @property iconBackgroundColor Background color for the coin icon
 * @property gradientColors List of colors for the coin's gradient background
 */
data class OnboardingCoin(
    val symbol: String,
    val name: String,
    val iconRes: DrawableResource,
    val iconBackgroundColor: Color,
    val gradientColors: List<Color>
)


/**
 * List of popular cryptocurrencies displayed during onboarding.
 * These coins are shown in the coin selection step, allowing users
 * to quickly add popular cryptocurrencies to their watchlist.
 * Each coin has unique gradient colors for visual distinction.
 */
val popularCoins = listOf(
    OnboardingCoin(
        symbol = "BTC",
        name = "Bitcoin",
        iconRes = Res.drawable.token__bitcoin,
        iconBackgroundColor = Color(0xFFF7931A), // Bitcoin Orange
        gradientColors = listOf(
            Color(0xFFF97316), // orange-500
            Color(0xFFEAB308)  // yellow-500
        )
    ),
    OnboardingCoin(
        symbol = "ETH",
        name = "Ethereum",
        iconRes = Res.drawable.token__ethereum_classic,
        iconBackgroundColor = Color(0xFF627EEA), // Ethereum Blue
        gradientColors = listOf(
            Color(0xFF3B82F6), // blue-500
            Color(0xFFA855F7)  // purple-500
        )
    ),
    OnboardingCoin(
        symbol = "BNB",
        name = "Binance",
        iconRes = Res.drawable.token__binance,
        iconBackgroundColor = Color(0xFFF3BA2F), // Binance Yellow
        gradientColors = listOf(
            Color(0xFFFACC15), // yellow-400
            Color(0xFFF97316)  // orange-500
        )
    ),
    OnboardingCoin(
        symbol = "SOL",
        name = "Solana",
        iconRes = Res.drawable.token__solana,
        iconBackgroundColor = Color(0xFF9945FF), // Solana Purple
        gradientColors = listOf(
            Color(0xFFA855F7), // purple-500
            Color(0xFFEC4899)  // pink-500
        )
    ),
    OnboardingCoin(
        symbol = "ADA",
        name = "Cardano",
        iconRes = Res.drawable.token__cardano,
        iconBackgroundColor = Color(0xFF0033AD), // Cardano Blue
        gradientColors = listOf(
            Color(0xFF60A5FA), // blue-400
            Color(0xFF22D3EE)  // cyan-500
        )
    ),
    OnboardingCoin(
        symbol = "XRP",
        name = "Ripple",
        iconRes = Res.drawable.token__xrp,
        iconBackgroundColor = Color(0xFF23292F), // XRP Dark Gray
        gradientColors = listOf(
            Color(0xFF9CA3AF), // gray-400
            Color(0xFF64748B)  // slate-500
        )
    )
)
