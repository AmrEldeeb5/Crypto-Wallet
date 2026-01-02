/**
 * OnboardingFeature.kt
 *
 * Defines the features and notification types displayed during onboarding.
 * Contains data classes and predefined lists for the welcome step features,
 * features grid, and notification configuration options.
 *
 * @see WelcomeStep for the welcome screen feature cards
 * @see FeaturesStep for the features grid display
 * @see NotificationsStep for notification type cards
 */
package com.example.valguard.app.onboarding.domain

import androidx.compose.ui.graphics.Color
import valguard.composeapp.generated.resources.Res
import valguard.composeapp.generated.resources.material_symbols__account_balance_wallet_outline
import valguard.composeapp.generated.resources.material_symbols__bar_chart_4_bars_rounded
import valguard.composeapp.generated.resources.material_symbols__finance_mode_rounded
import valguard.composeapp.generated.resources.material_symbols__notifications_outline
import valguard.composeapp.generated.resources.material_symbols__shield_outline_rounded
import org.jetbrains.compose.resources.DrawableResource

/**
 * Enum representing icon types used in onboarding feature cards.
 *
 * Each icon type maps to a drawable resource for cross-platform display.
 *
 * @property resource The drawable resource representing this icon
 */
enum class OnboardingIcon(val resource: DrawableResource) {

    ANALYTICS (Res.drawable.material_symbols__bar_chart_4_bars_rounded),
    TRENDING_UP(Res.drawable.material_symbols__finance_mode_rounded),
    NOTIFICATIONS(Res.drawable.material_symbols__notifications_outline),
    WALLET(Res.drawable.material_symbols__account_balance_wallet_outline),
    SHIELD(Res.drawable.material_symbols__shield_outline_rounded)
}

data class OnboardingFeature(
    val iconType: OnboardingIcon,
    val title: String,
    val description: String,
    val gradientColors: List<Color>
)

val welcomeFeatures = listOf(
    OnboardingFeature(
        iconType = OnboardingIcon.TRENDING_UP,
        title = "Track 5000+ Cryptos",
        description = "Real-time market data",
        gradientColors = listOf(
            Color(0xFF34D399), // emerald-400
            Color(0xFF14B8A6)  // teal-500
        )
    ),
    OnboardingFeature(
        iconType = OnboardingIcon.NOTIFICATIONS,
        title = "Smart Price Alerts",
        description = "Never miss opportunities",
        gradientColors = listOf(
            Color(0xFF3B82F6), // blue-500
            Color(0xFFA855F7)  // purple-500
        )
    ),
    OnboardingFeature(
        iconType = OnboardingIcon.WALLET,
        title = "Portfolio Management",
        description = "Track your investments",
        gradientColors = listOf(
            Color(0xFFEC4899), // pink-500
            Color(0xFFFB7185)  // rose-400
        )
    )
)

/**
 * Features displayed in the 2x2 grid on the features step.
 *
 * These provide more detailed information about app capabilities
 * with consistent blue-purple gradient theming.
 */
val gridFeatures = listOf(
    OnboardingFeature(
        iconType = OnboardingIcon.ANALYTICS,
        title = "Real-Time Prices",
        description = "Live updates every second",
        gradientColors = listOf(
            Color(0xFF2563EB), // blue-600
            Color(0xFF9333EA)  // purple-600
        )
    ),
    OnboardingFeature(
        iconType = OnboardingIcon.NOTIFICATIONS,
        title = "Smart Alerts",
        description = "Custom price notifications",
        gradientColors = listOf(
            Color(0xFF2563EB), // blue-600
            Color(0xFF9333EA)  // purple-600
        )
    ),
    OnboardingFeature(
        iconType = OnboardingIcon.WALLET,
        title = "Portfolio Tracking",
        description = "Monitor your investments",
        gradientColors = listOf(
            Color(0xFF2563EB), // blue-600
            Color(0xFF9333EA)  // purple-600
        )
    ),
    OnboardingFeature(
        iconType = OnboardingIcon.SHIELD,
        title = "Secure & Private",
        description = "Your data stays yours",
        gradientColors = listOf(
            Color(0xFF2563EB), // blue-600
            Color(0xFF9333EA)  // purple-600
        )
    )
)





