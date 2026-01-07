package com.example.valguard.app.onboarding.data
 
 import com.example.valguard.app.core.database.preferences.PreferenceDao
 import com.example.valguard.app.core.database.preferences.PreferenceKeys
 import com.example.valguard.app.core.database.preferences.PreferenceEntity
 import com.example.valguard.app.onboarding.presentation.OnboardingState
 import com.example.valguard.app.watchlist.domain.WatchlistRepository
 
 /**
  * Repository for managing onboarding state persistence and watchlist integration.
  */
 interface OnboardingRepository {
     suspend fun getOnboardingState(): OnboardingState
     suspend fun saveOnboardingState(state: OnboardingState)
     suspend fun isOnboardingCompleted(): Boolean
     suspend fun setOnboardingCompleted(completed: Boolean)
     suspend fun saveSelectedCoinsToWatchlist(coins: Set<String>)
 }
 

 
 class OnboardingRepositoryImpl(
     private val preferenceDao: PreferenceDao,
     private val watchlistRepository: WatchlistRepository
 ) : OnboardingRepository {
 
     // In-memory storage for transient state during the flow
     private var savedCurrentStep: Int = 0
     private var savedSelectedCoins: Set<String> = emptySet()
     private var savedNotificationsEnabled: Boolean = false
 
     override suspend fun getOnboardingState(): OnboardingState {
         return OnboardingState(
             currentStep = savedCurrentStep,
             selectedCoins = savedSelectedCoins,
             notificationsEnabled = savedNotificationsEnabled,
             isLoading = false
         )
     }
 
     override suspend fun saveOnboardingState(state: OnboardingState) {
         savedCurrentStep = state.currentStep
         savedSelectedCoins = state.selectedCoins
         savedNotificationsEnabled = state.notificationsEnabled
     }
 
     override suspend fun isOnboardingCompleted(): Boolean {
         return preferenceDao
             .getValue(PreferenceKeys.ONBOARDING_COMPLETED)
             ?.toBooleanStrictOrNull()
             ?: false
     }
 
     override suspend fun setOnboardingCompleted(completed: Boolean) {
         preferenceDao.setPreference(
             PreferenceEntity(
                 key = PreferenceKeys.ONBOARDING_COMPLETED,
                 value = completed.toString()
             )
         )
     }
 
     override suspend fun saveSelectedCoinsToWatchlist(coins: Set<String>) {
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
