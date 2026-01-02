package com.example.valguard.app.splash.data

import com.example.valguard.app.splash.domain.DeviceCapabilityDetector

/**
 * Platform-specific factory for DeviceCapabilityDetector.
 * Implemented separately for Android and iOS.
 */
expect fun createDeviceCapabilityDetector(): DeviceCapabilityDetector
