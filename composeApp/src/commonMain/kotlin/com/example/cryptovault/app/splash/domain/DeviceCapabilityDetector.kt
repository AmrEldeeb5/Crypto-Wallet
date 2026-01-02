package com.example.cryptovault.app.splash.domain

/**
 * Detects device performance tier and accessibility settings.
 * Used to scale splash screen animations appropriately.
 */
interface DeviceCapabilityDetector {
    /**
     * Detects current device capabilities.
     * Should be called once at app startup.
     */
    fun detect(): DeviceCapabilities
}
