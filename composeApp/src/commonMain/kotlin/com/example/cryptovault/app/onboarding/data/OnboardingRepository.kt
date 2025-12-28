/**
 * OnboardingRepository.kt
 *
 * Repository for managing onboarding state persistence and watchlist integration.
 * Handles saving/loading onboarding progress and transferring selected coins
 * to the user's watchlist upon completion.
 *
 * Currently uses in-memory storage for MVP; should be replaced with
 * DataStore for production persistence.
 *
 * @see OnboardingViewModel for the consumer of this repository
 * @see WatchlistRepository for watchlist integration
 */
package com.example.cryptovault.app.onboarding.data

import com.example.cryptovault.app.onboarding.presentation.OnboardingState
import com.example.cryptovault.app.watchlist.domain.WatchlistRepository
import kotlinx.coroutines.flow.first

/**
 * Repository for onboarding data operations.
 *
 * Manages the persistence of onboarding state including current step,
 * selected coins, and notification preferences. Also handles the
 * transfer of selected coins to the watchlist upon completion.
 *
 * @property watchlistRepository Repository for adding coins to watchlist
 */
class OnboardingRepository(
    private val watchlistRepository: WatchlistRepository
) {
    // In-memory storage for MVP
    // TODO: Replace with DataStore for production
    private var savedCurrentStep: Int = 0
    private var savedSelectedCoins: Set<String> = emptySet()
    private var savedNotificationsEnabled: Boolean = false
    private var onboardingCompleted: Boolean = false
    
    /**
     * Retrieves the saved onboarding state.
     *
     * @return [OnboardingState] with saved progress, or default state if none saved
     */
    suspend fun getOnboardingState(): OnboardingState {
        return OnboardingState(
            currentStep = savedCurrentStep,
            selectedCoins = savedSelectedCoins,
            notificationsEnabled = savedNotificationsEnabled,
            isLoading = false
        )
    }
    
    /**
     * Saves the current onboarding state for later restoration.
     *
     * @param state The [OnboardingState] to persist
     */
    suspend fun saveOnboardingState(state: OnboardingState) {
        savedCurrentStep = state.currentStep
        savedSelectedCoins = state.selectedCoins
        savedNotificationsEnabled = state.notificationsEnabled
    }
    
    /**
     * Marks onboarding as completed or not completed.
     *
     * @param completed True if onboarding is finished
     */
    suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingCompleted = completed
    }
    
    /**
     * Checks if the user has completed onboarding.
     *
     * @return True if onboarding has been completed
     */
    suspend fun isOnboardingCompleted(): Boolean {
        return onboardingCompleted
    }
    
    /**
     * Saves selected coins to the user's watchlist.
     *
     * Maps coin symbols (e.g., "BTC") to CoinGecko API IDs (e.g., "bitcoin")
     * and adds each to the watchlist. Continues even if individual additions fail.
     *
     * @param coins Set of coin symbols selected during onboarding
     */
    suspend fun saveSelectedCoinsToWatchlist(coins: Set<String>) {
        // Map coin symbols to coin IDs (lowercase for API compatibility)
        val coinIdMap = mapOf(
            "BTC" to "bitcoin",
            "ETH" to "ethereum",
            "BNB" to "binancecoin",
            "SOL" to "solana",
            "ADA" to "cardano",
            "XRP" to "ripple"
        )
        
        coins.forEach { symbol ->
            val coinId = coinIdMap[symbol] ?: symbol.lowercase()
            try {
                watchlistRepository.addToWatchlist(coinId)
            } catch (e: Exception) {
                // Continue even if one fails
            }
        }
    }
}
