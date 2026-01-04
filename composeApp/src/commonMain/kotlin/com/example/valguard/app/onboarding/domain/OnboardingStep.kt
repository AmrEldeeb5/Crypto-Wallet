/**
 * OnboardingStep.kt
 *
 * Defines the steps in the onboarding flow as a sealed class hierarchy.
 * Each step has an associated index and gradient colors for visual theming.
 *
 * The onboarding flow consists of 4 steps:
 * 1. Welcome - Introduction to the app
 * 2. Features - Showcase of app capabilities
 * 3. CoinSelection - User selects favorite cryptocurrencies
 * 4. Notifications - Configure notification preferences
 *
 * @see OnboardingScreen for the main onboarding UI
 * @see OnboardingViewModel for step navigation logic
 */
package com.example.valguard.app.onboarding.domain

import androidx.compose.ui.graphics.Color

/**
 * Sealed class representing the steps in the onboarding flow.
 *
 * Each step contains visual theming information (gradient colors) and
 * an index for navigation purposes. Use [fromIndex] to convert an
 * integer index to the corresponding step.
 *
 * @property index The zero-based index of this step (0-3)
 * @property gradientColors List of colors for the step's gradient theme
 */
sealed class OnboardingStep(
    val index: Int,
    val gradientColors: List<Color>
) {
    /**
     * Welcome step - First step introducing users to Valguard.
     * Uses blue-to-purple gradient theme.
     */
    data object Welcome : OnboardingStep(
        index = 0,
        gradientColors = listOf(
            Color(0xFF3B82F6), // blue-500
            Color(0xFFA855F7)  // purple-500
        )
    )
    
    /**
     * Features step - Showcases app capabilities and features.
     * Uses purple-to-pink gradient theme.
     */
    data object Features : OnboardingStep(
        index = 1,
        gradientColors = listOf(
            Color(0xFFA855F7), // purple-500
            Color(0xFFEC4899)  // pink-500
        )
    )
    
    /**
     * CoinSelection step - User selects favorite cryptocurrencies for watchlist.
     * Uses pink-to-rose gradient theme.
     */
    data object CoinSelection : OnboardingStep(
        index = 2,
        gradientColors = listOf(
            Color(0xFFEC4899), // pink-500
            Color(0xFFFB7185)  // rose-400
        )
    )
    
    /**
     * Notifications step - Final step for configuring notification preferences.
     * Uses emerald-to-teal gradient theme.
     */
    data object Notifications : OnboardingStep(
        index = 3,
        gradientColors = listOf(
            Color(0xFF3B82F6), // blue-500
            Color(0xFFA855F7)  // purple-500
        )
    )
    
    companion object {
        /** Total number of steps in the onboarding flow */
        const val TOTAL_STEPS = 4
        
        /**
         * Converts an integer index to the corresponding [OnboardingStep].
         *
         * @param index The step index (0-3)
         * @return The corresponding OnboardingStep, defaults to [Welcome] for invalid indices
         */
        fun fromIndex(index: Int): OnboardingStep = when (index) {
            0 -> Welcome
            1 -> Features
            2 -> CoinSelection
            3 -> Notifications
            else -> Welcome
        }
        
        /**
         * Returns all onboarding steps in order.
         *
         * @return List of all [OnboardingStep] instances in sequential order
         */
        fun all(): List<OnboardingStep> = listOf(Welcome, Features, CoinSelection, Notifications)
    }
}
