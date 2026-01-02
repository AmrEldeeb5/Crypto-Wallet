package com.example.valguard.app.splash.data

import kotlinx.coroutines.delay

/**
 * Real implementation of UI readiness checker.
 * Signals that UI is ready to render.
 */
class RealUIReadinessChecker : UIReadinessChecker {
    override suspend fun check() {
        // Simulate UI readiness check
        // In production, this would:
        // - Verify all critical resources loaded
        // - Check that main screen can be rendered
        // - Signal readiness to navigation system
        delay(200) // Simulate work
    }
}
