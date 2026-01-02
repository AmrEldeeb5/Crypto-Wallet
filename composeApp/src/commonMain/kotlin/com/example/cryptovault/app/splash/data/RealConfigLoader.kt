package com.example.cryptovault.app.splash.data

import kotlinx.coroutines.delay

/**
 * Real implementation of configuration loader.
 * Loads feature flags and app configuration.
 */
class RealConfigLoader : ConfigLoader {
    override suspend fun load() {
        // Simulate config loading
        // In production, this would:
        // - Load feature flags from remote config
        // - Load app configuration
        // - Use cached config if fetch fails
        // - Apply default configuration as fallback
        delay(300) // Simulate work
    }
}
