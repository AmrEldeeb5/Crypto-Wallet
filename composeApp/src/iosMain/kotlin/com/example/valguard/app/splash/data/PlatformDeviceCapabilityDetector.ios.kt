package com.example.valguard.app.splash.data

import com.example.valguard.app.splash.domain.DeviceCapabilityDetector

/**
 * iOS implementation of platform-specific detector factory.
 */
actual fun createDeviceCapabilityDetector(): DeviceCapabilityDetector {
    return IosDeviceCapabilityDetector()
}
