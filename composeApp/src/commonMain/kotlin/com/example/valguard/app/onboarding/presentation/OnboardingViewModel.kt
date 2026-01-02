/**
 * OnboardingViewModel.kt
 *
 * ViewModel managing the onboarding flow state and business logic.
 * Handles step navigation, coin selection, notification preferences,
 * and completion with watchlist integration.
 *
 * Features:
 * - Step-by-step navigation with validation
 * - Coin selection with persistence
 * - Skip functionality with confirmation
 * - Success animation on completion
 * - Automatic state persistence
 *
 * @see OnboardingScreen for the UI consumer
 * @see OnboardingState for the state model
 * @see OnboardingEvent for user interactions
 */
package com.example.valguard.app.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.onboarding.data.OnboardingRepository
import com.example.valguard.app.onboarding.domain.OnboardingStep
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the onboarding flow.
 *
 * Manages the complete onboarding experience including navigation between
 * steps, user selections, and completion logic. Persists state through
 * [OnboardingRepository] and transfers selected coins to watchlist on completion.
 *
 * @property onboardingRepository Repository for state persistence and watchlist integration
 */
class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(OnboardingState())
    
    /** Current onboarding state as an observable flow */
    val state: StateFlow<OnboardingState> = _state.asStateFlow()
    
    /**
     * Whether the user can proceed to the next step.
     * On coin selection step, requires at least one coin selected.
     */
    val canProceed: StateFlow<Boolean> = _state.map { state ->
        when (state.currentStep) {
            2 -> state.selectedCoins.isNotEmpty()
            else -> true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    /**
     * Gradient colors for the current step's theme.
     * Updates automatically when step changes.
     */
    val currentStepColors: StateFlow<List<androidx.compose.ui.graphics.Color>> = _state.map { state ->
        OnboardingStep.fromIndex(state.currentStep).gradientColors
    }.stateIn(
        viewModelScope, 
        SharingStarted.WhileSubscribed(5000), 
        OnboardingStep.Welcome.gradientColors
    )
    
    // Navigation callback to be set by the screen
    private var onNavigateToMain: (() -> Unit)? = null
    
    init {
        loadSavedState()
    }
    
    /**
     * Sets the callback for navigating to the main screen.
     * Called by [OnboardingScreen] to provide navigation capability.
     *
     * @param callback Function to invoke when navigation is needed
     */
    fun setNavigationCallback(callback: () -> Unit) {
        onNavigateToMain = callback
    }
    
    private fun loadSavedState() {
        viewModelScope.launch {
            try {
                val savedState = onboardingRepository.getOnboardingState()
                _state.update { savedState.copy(isLoading = false) }
            } catch (e: Exception) {
                // If loading fails, start fresh
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    
    /**
     * Handles user interaction events.
     *
     * Dispatches events to appropriate handler methods based on event type.
     *
     * @param event The [OnboardingEvent] to process
     */
    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.NextStep -> handleNextStep()
            is OnboardingEvent.PreviousStep -> handlePreviousStep()
            is OnboardingEvent.SkipToEnd -> showSkipConfirmation()
            is OnboardingEvent.ConfirmSkip -> confirmSkip()
            is OnboardingEvent.DismissSkipDialog -> dismissSkipDialog()
            is OnboardingEvent.ToggleCoin -> toggleCoin(event.coinSymbol)
            is OnboardingEvent.ToggleNotifications -> toggleNotifications()
            is OnboardingEvent.CompleteOnboarding -> completeOnboarding()
            is OnboardingEvent.NavigateToMain -> navigateToMain()
        }
    }
    
    private fun handleNextStep() {
        val currentState = _state.value
        if (currentState.currentStep < 3 && !currentState.isTransitioning) {
            // Check if can proceed (coin selection validation)
            if (currentState.currentStep == 2 && currentState.selectedCoins.isEmpty()) {
                return
            }
            
            _state.update { it.copy(isTransitioning = true) }
            viewModelScope.launch {
                delay(TRANSITION_DURATION_MS)
                _state.update { 
                    it.copy(
                        currentStep = it.currentStep + 1,
                        isTransitioning = false
                    )
                }
                saveState()
            }
        } else if (currentState.currentStep == 3) {
            completeOnboarding()
        }
    }
    
    private fun handlePreviousStep() {
        val currentState = _state.value
        if (currentState.currentStep > 0 && !currentState.isTransitioning) {
            _state.update { it.copy(isTransitioning = true) }
            viewModelScope.launch {
                delay(TRANSITION_DURATION_MS)
                _state.update { 
                    it.copy(
                        currentStep = it.currentStep - 1,
                        isTransitioning = false
                    )
                }
                saveState()
            }
        }
    }
    
    private fun showSkipConfirmation() {
        _state.update { it.copy(showSkipDialog = true) }
    }
    
    private fun confirmSkip() {
        _state.update { 
            it.copy(
                showSkipDialog = false,
                isTransitioning = true
            )
        }
        viewModelScope.launch {
            delay(TRANSITION_DURATION_MS)
            _state.update { 
                it.copy(
                    currentStep = 3,
                    isTransitioning = false
                )
            }
            saveState()
        }
    }
    
    private fun dismissSkipDialog() {
        _state.update { it.copy(showSkipDialog = false) }
    }
    
    private fun toggleCoin(coinSymbol: String) {
        _state.update { state ->
            val newSelectedCoins = if (state.selectedCoins.contains(coinSymbol)) {
                state.selectedCoins - coinSymbol
            } else {
                state.selectedCoins + coinSymbol
            }
            state.copy(selectedCoins = newSelectedCoins)
        }
        viewModelScope.launch {
            saveState()
        }
    }
    
    private fun toggleNotifications() {
        _state.update { it.copy(notificationsEnabled = !it.notificationsEnabled) }
        viewModelScope.launch {
            saveState()
        }
    }
    
    private fun completeOnboarding() {
        _state.update { it.copy(showSuccessAnimation = true) }
        viewModelScope.launch {
            try {
                // Save selected coins to watchlist
                onboardingRepository.saveSelectedCoinsToWatchlist(_state.value.selectedCoins)
                // Mark onboarding as completed
                onboardingRepository.setOnboardingCompleted(true)
            } catch (e: Exception) {
                // Continue even if saving fails
            }
            
            // Wait for success animation
            delay(SUCCESS_ANIMATION_DURATION_MS)
            
            // Navigate to main screen
            navigateToMain()
        }
    }
    
    private fun navigateToMain() {
        onNavigateToMain?.invoke()
    }
    
    private suspend fun saveState() {
        try {
            onboardingRepository.saveOnboardingState(_state.value)
        } catch (e: Exception) {
            // Non-blocking - continue even if save fails
        }
    }
    
    companion object {
        /** Duration of step transition animations in milliseconds */
        private const val TRANSITION_DURATION_MS = 300L
        /** Duration of success animation before navigation in milliseconds */
        private const val SUCCESS_ANIMATION_DURATION_MS = 1500L
    }
}
