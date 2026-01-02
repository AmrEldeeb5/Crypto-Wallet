package com.example.cryptovault.app.splash.data

import android.content.Context
import com.example.cryptovault.app.splash.domain.DeviceCapabilityDetector

/**
 * Android implementation of platform-specific detector factory.
 * Requires Android context for capability detection.
 */
private lateinit var applicationContext: Context

fun initializeDeviceCapabilityDetector(context: Context) {
    applicationContext = context.applicationContext
}

actual fun createDeviceCapabilityDetector(): DeviceCapabilityDetector {
    return AndroidDeviceCapabilityDetector(applicationContext)
}
