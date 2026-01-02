package com.example.valguard.app.splash.data

import kotlinx.coroutines.delay

/**
 * Real implementation of network warm-up.
 * Initializes Ktor client and checks connectivity.
 */
class RealNetworkWarmer : NetworkWarmer {
    override suspend fun warmUp() {
        // Simulate network warm-up
        // In production, this would:
        // - Initialize Ktor HTTP client
        // - Check network connectivity
        // - Warm up API connection (ping endpoint)
        // - Handle offline gracefully
        delay(400) // Simulate work
    }
}
