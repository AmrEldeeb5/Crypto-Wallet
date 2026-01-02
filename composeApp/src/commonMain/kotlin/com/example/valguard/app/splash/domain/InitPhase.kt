package com.example.valguard.app.splash.domain

/**
 * Initialization phases with progress mapping.
 * Each phase represents a stage of app startup.
 */
sealed class InitPhase(val progressRange: ClosedFloatingPointRange<Float>) {
    object SecureStorage : InitPhase(0.0f..0.30f)
    object Database : InitPhase(0.30f..0.55f)
    object Network : InitPhase(0.55f..0.75f)
    object Config : InitPhase(0.75f..0.90f)
    object UIReady : InitPhase(0.90f..1.0f)
    
    companion object {
        fun fromProgress(progress: Float): InitPhase = when {
            progress < 0.30f -> SecureStorage
            progress < 0.55f -> Database
            progress < 0.75f -> Network
            progress < 0.90f -> Config
            else -> UIReady
        }
    }
}
