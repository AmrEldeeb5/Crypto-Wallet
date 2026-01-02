package com.example.valguard.app.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.splash.domain.DeviceCapabilityDetector
import com.example.valguard.app.splash.domain.InitPhase
import com.example.valguard.app.splash.domain.InitializationOrchestrator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for splash screen.
 * Orchestrates initialization and manages UI state.
 */
class SplashViewModel(
    private val initOrchestrator: InitializationOrchestrator,
    private val capabilityDetector: DeviceCapabilityDetector
) : ViewModel() {
    
    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()
    
    private var lastAnnouncedProgress = -1f
    
    init {
        // Detect device capabilities immediately
        val capabilities = capabilityDetector.detect()
        _state.update { it.copy(deviceCapabilities = capabilities) }
    }
    
    /**
     * Starts the initialization sequence.
     */
    fun startInitialization() {
        viewModelScope.launch {
            val result = initOrchestrator.initialize { progress, phase ->
                _state.update { 
                    it.copy(
                        progress = progress,
                        currentPhase = phase
                    )
                }
                
                // Throttle TalkBack announcements
                // Only announce at 0%, 50%, 100%, or phase changes
                val shouldAnnounce = when {
                    progress == 0f && lastAnnouncedProgress != 0f -> true
                    progress >= 0.5f && lastAnnouncedProgress < 0.5f -> true
                    progress >= 1.0f && lastAnnouncedProgress < 1.0f -> true
                    else -> false
                }
                
                if (shouldAnnounce) {
                    lastAnnouncedProgress = progress
                }
            }
            
            result.fold(
                onSuccess = {
                    _state.update { it.copy(isComplete = true) }
                },
                onFailure = { error ->
                    handleError(SplashError.Fatal(error.message ?: "Unknown error"))
                }
            )
        }
    }
    
    /**
     * Handles timeout for a specific phase.
     */
    fun handleTimeout(phase: InitPhase) {
        _state.update { 
            it.copy(error = SplashError.Timeout(phase))
        }
    }
    
    /**
     * Handles fatal errors.
     */
    fun handleError(error: SplashError) {
        _state.update { 
            it.copy(error = error)
        }
    }
}
