/**
 * OnboardingState.kt
 *
 * Defines the UI state and events for the onboarding flow.
 * Contains the [OnboardingState] data class representing the current
 * state of the onboarding process, and [OnboardingEvent] sealed class
 * for user interactions.
 *
 * @see OnboardingViewModel for state management
 * @see OnboardingScreen for the UI consumer
 */
package com.example.cryptovault.app.onboarding.presentation

/**
 * Data class representing the complete UI state of the onboarding flow.
 *
 * Contains all information needed to render the onboarding screens,
 * including navigation state, user selections, and UI flags.
 *
 * @property currentStep The current step index (0-3)
 * @property selectedCoins Set of coin symbols selected by the user
 * @property notificationsEnabled Whether notifications are enabled
 * @property isTransitioning True during step transition animations
 * @property showSkipDialog True when skip confirmation dialog is visible
 * @property showSuccessAnimation True when completion animation is playing
 * @property isLoading True while loading saved state
 */
data class OnboardingState(
    val currentStep: Int = 0,
    val selectedCoins: Set<String> = emptySet(),
    val notificationsEnabled: Boolean = false,
    val isTransitioning: Boolean = false,
    val showSkipDialog: Boolean = false,
    val showSuccessAnimation: Boolean = false,
    val isLoading: Boolean = true
) {
    /** Progress percentage (25%, 50%, 75%, 100%) based on current step */
    val progressPercentage: Int
        get() = ((currentStep + 1) * 25)
    
    /** Human-readable step indicator text (e.g., "Step 1 of 4") */
    val stepText: String
        get() = "Step ${currentStep + 1} of 4"
    
    /** Number of progress bar segments to fill */
    val filledSegments: Int
        get() = currentStep + 1
    
    /** True if on the final step (notifications) */
    val isLastStep: Boolean
        get() = currentStep == 3
    
    /** Text for the primary action button */
    val buttonText: String
        get() = if (isLastStep) "Get Started" else "Continue"
    
    /** True if back navigation should be shown */
    val showBackButton: Boolean
        get() = currentStep > 0
    
    /** True if skip option should be shown */
    val showSkipButton: Boolean
        get() = currentStep < 3
    
    /** Number of coins currently selected */
    val selectedCoinsCount: Int
        get() = selectedCoins.size
    
    /** Badge text showing selection count (e.g., "✓ 3 coins selected") */
    val selectionBadgeText: String
        get() = "✓ $selectedCoinsCount ${if (selectedCoinsCount == 1) "coin" else "coins"} selected"
}

/**
 * Sealed class representing user interactions in the onboarding flow.
 *
 * Each event corresponds to a user action that the [OnboardingViewModel]
 * handles to update state or trigger side effects.
 */
sealed class OnboardingEvent {
    /** Navigate to the next step */
    data object NextStep : OnboardingEvent()
    
    /** Navigate to the previous step */
    data object PreviousStep : OnboardingEvent()
    
    /** Request to skip to the end (shows confirmation dialog) */
    data object SkipToEnd : OnboardingEvent()
    
    /** Confirm skipping onboarding */
    data object ConfirmSkip : OnboardingEvent()
    
    /** Dismiss the skip confirmation dialog */
    data object DismissSkipDialog : OnboardingEvent()
    
    /**
     * Toggle selection state of a coin.
     * @property coinSymbol The symbol of the coin to toggle (e.g., "BTC")
     */
    data class ToggleCoin(val coinSymbol: String) : OnboardingEvent()
    
    /** Toggle notification preferences */
    data object ToggleNotifications : OnboardingEvent()
    
    /** Complete onboarding and save selections */
    data object CompleteOnboarding : OnboardingEvent()
    
    /** Navigate to the main app screen */
    data object NavigateToMain : OnboardingEvent()
}
