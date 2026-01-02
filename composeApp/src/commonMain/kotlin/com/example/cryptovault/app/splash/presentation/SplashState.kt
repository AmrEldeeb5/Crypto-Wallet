package com.example.cryptovault.app.splash.presentation

import com.example.cryptovault.app.splash.domain.DeviceCapabilities
import com.example.cryptovault.app.splash.domain.InitPhase

/**
 * UI state for splash screen.
 */
data class SplashState(
    val progress: Float = 0f,
    val currentPhase: InitPhase = InitPhase.SecureStorage,
    val isComplete: Boolean = false,
    val error: SplashError? = null,
    val deviceCapabilities: DeviceCapabilities = DeviceCapabilities.Unknown
)

/**
 * Splash screen errors.
 */
sealed class SplashError {
    data class Timeout(val phase: InitPhase) : SplashError()
    data class Fatal(val message: String) : SplashError()
}
