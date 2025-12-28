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
package com.example.cryptovault.app.onboarding.domain

import androidx.compose.ui.graphics.Color

/**
 * Enum representing icon types used in onboarding feature cards.
 *
 * Each icon type maps to an emoji for cross-platform display.
 *
 * @property emoji The emoji character representing this icon
 */
enum class OnboardingIcon(val emoji: String) {
    /** Trending/chart icon for price-related features */
    TRENDING_UP("📈"),
    /** Bell icon for notification features */
    NOTIFICATIONS("🔔"),
    /** Wallet icon for portfolio features */
    WALLET("💰"),
    /** Shield icon for security features */
    SHIELD("🛡️"),
    /** Sparkles icon for special/premium features */
    SPARKLES("✨")
}

/**
 * Data class representing a feature displayed during onboarding.
 *
 * @property iconType The type of icon to display
 * @property title The feature title text
 * @property description Brief description of the feature
 * @property gradientColors Colors for the icon's gradient background
 */
data class OnboardingFeature(
    val iconType: OnboardingIcon,
    val title: String,
    val description: String,
    val gradientColors: List<Color>
)

/**
 * Features displayed on the welcome step.
 *
 * These are the main value propositions shown when users first
 * open the onboarding flow, highlighting key app capabilities.
 */
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
        iconType = OnboardingIcon.TRENDING_UP,
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

/**
 * Data class representing a notification type option.
 *
 * Used in the notifications step to show different notification
 * categories users can enable.
 *
 * @property iconType The type of icon to display
 * @property title The notification type title
 * @property description Explanation of what this notification does
 * @property statusText Text showing notification frequency/timing
 * @property gradientColors Colors for the icon's gradient background
 */
data class NotificationType(
    val iconType: OnboardingIcon,
    val title: String,
    val description: String,
    val statusText: String,
    val gradientColors: List<Color>
)

/**
 * Notification types displayed on the notifications step.
 *
 * Shows the different categories of notifications users can
 * receive, each with timing/frequency information.
 */
val notificationTypes = listOf(
    NotificationType(
        iconType = OnboardingIcon.TRENDING_UP,
        title = "Price Movement Alerts",
        description = "Get notified when prices hit your targets",
        statusText = "Real-time notifications",
        gradientColors = listOf(
            Color(0xFF34D399), // emerald-400
            Color(0xFF14B8A6)  // teal-500
        )
    ),
    NotificationType(
        iconType = OnboardingIcon.WALLET,
        title = "Portfolio Updates",
        description = "Daily summaries of your portfolio performance",
        statusText = "Daily at 9:00 AM",
        gradientColors = listOf(
            Color(0xFF3B82F6), // blue-500
            Color(0xFFA855F7)  // purple-500
        )
    ),
    NotificationType(
        iconType = OnboardingIcon.SPARKLES,
        title = "Market Insights",
        description = "Important news and market movements",
        statusText = "As they happen",
        gradientColors = listOf(
            Color(0xFFEC4899), // pink-500
            Color(0xFFFB7185)  // rose-400
        )
    )
)
