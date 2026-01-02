package com.example.valguard.app.splash.domain

/**
 * Device capability configuration detected at startup.
 * Used to scale animations and effects based on device performance tier.
 */
data class DeviceCapabilities(
    val isLowRam: Boolean = false,              // < 2GB RAM
    val isLowRefreshRate: Boolean = false,      // < 90Hz
    val isOldGpu: Boolean = false,              // GPU tier < 3
    val reduceMotionEnabled: Boolean = false    // System accessibility setting
) {
    companion object {
        val Unknown = DeviceCapabilities()
    }
}
