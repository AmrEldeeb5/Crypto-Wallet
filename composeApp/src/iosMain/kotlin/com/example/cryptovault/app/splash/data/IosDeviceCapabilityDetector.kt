package com.example.cryptovault.app.splash.data

import com.example.cryptovault.app.splash.domain.DeviceCapabilities
import com.example.cryptovault.app.splash.domain.DeviceCapabilityDetector
import platform.UIKit.UIAccessibility
import platform.UIKit.UIScreen
import platform.darwin.NSProcessInfo

/**
 * iOS implementation of device capability detection.
 * Detects RAM, refresh rate, and reduce motion setting.
 */
class IosDeviceCapabilityDetector : DeviceCapabilityDetector {
    
    override fun detect(): DeviceCapabilities {
        return DeviceCapabilities(
            isLowRam = detectLowRam(),
            isLowRefreshRate = detectLowRefreshRate(),
            isOldGpu = detectOldGpu(),
            reduceMotionEnabled = detectReduceMotion()
        )
    }
    
    private fun detectLowRam(): Boolean {
        val processInfo = NSProcessInfo.processInfo
        val physicalMemory = processInfo.physicalMemory
        
        // Convert to GB and check if < 2GB
        val totalRamGB = physicalMemory / (1024.0 * 1024.0 * 1024.0)
        return totalRamGB < 2.0
    }
    
    private fun detectLowRefreshRate(): Boolean {
        val mainScreen = UIScreen.mainScreen
        val refreshRate = mainScreen.maximumFramesPerSecond.toFloat()
        
        // Consider < 90Hz as low refresh rate
        // Most iOS devices are 60Hz or 120Hz (ProMotion)
        return refreshRate < 90f
    }
    
    private fun detectOldGpu(): Boolean {
        // On iOS, we can use device model as a proxy for GPU capability
        // Older devices (pre-A12) are considered low-end
        // This is a simplified heuristic
        val processInfo = NSProcessInfo.processInfo
        val processorCount = processInfo.processorCount
        
        // Devices with fewer cores tend to have older GPUs
        // A12 and newer typically have 6+ cores
        return processorCount < 6
    }
    
    private fun detectReduceMotion(): Boolean {
        return UIAccessibility.isReduceMotionEnabled()
    }
}
