package com.example.cryptovault.app.splash.data

import com.example.cryptovault.app.splash.domain.DeviceCapabilityDetector

/**
 * Platform-specific factory for DeviceCapabilityDetector.
 * Implemented separately for Android and iOS.
 */
expect fun createDeviceCapabilityDetector(): DeviceCapabilityDetector
