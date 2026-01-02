package com.example.cryptovault.app.splash.data

import com.example.cryptovault.app.splash.domain.DeviceCapabilityDetector

/**
 * iOS implementation of platform-specific detector factory.
 */
actual fun createDeviceCapabilityDetector(): DeviceCapabilityDetector {
    return IosDeviceCapabilityDetector()
}
