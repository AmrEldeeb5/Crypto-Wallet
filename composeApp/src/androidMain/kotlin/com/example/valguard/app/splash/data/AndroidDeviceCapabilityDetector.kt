package com.example.valguard.app.splash.data

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.Display
import com.example.valguard.app.splash.domain.DeviceCapabilities
import com.example.valguard.app.splash.domain.DeviceCapabilityDetector

/**
 * Android implementation of device capability detection.
 * Detects RAM, refresh rate, GPU tier, and reduce motion setting.
 */
class AndroidDeviceCapabilityDetector(
    private val context: Context
) : DeviceCapabilityDetector {
    
    override fun detect(): DeviceCapabilities {
        return DeviceCapabilities(
            isLowRam = detectLowRam(),
            isLowRefreshRate = detectLowRefreshRate(),
            isOldGpu = detectOldGpu(),
            reduceMotionEnabled = detectReduceMotion()
        )
    }
    
    private fun detectLowRam(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        
        // Consider < 2GB as low RAM
        val totalRamGB = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        return totalRamGB < 2.0
    }
    
    private fun detectLowRefreshRate(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Use WindowManager for all supported versions
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? android.view.WindowManager
                @Suppress("DEPRECATION")
                val refreshRate = windowManager?.defaultDisplay?.refreshRate ?: 60f
                // Consider < 90Hz as low refresh rate
                refreshRate < 90f
            } else {
                // Assume 60Hz for older devices
                true
            }
        } catch (e: Exception) {
            // If we can't detect, assume low refresh rate (safer default)
            true
        }
    }
    
    private fun detectOldGpu(): Boolean {
        // Use GPU renderer string to estimate tier
        // This is a simplified heuristic - in production, you might use
        // more sophisticated detection or a device database
        val renderer = android.opengl.GLES20.glGetString(android.opengl.GLES20.GL_RENDERER) ?: ""
        
        // Check for known low-end GPU patterns
        val lowEndPatterns = listOf(
            "Adreno 3",  // Adreno 3xx series
            "Adreno 4",  // Adreno 4xx series
            "Mali-4",    // Mali-4xx series
            "Mali-T",    // Mali-Txx series
            "PowerVR SGX"
        )
        
        return lowEndPatterns.any { pattern -> 
            renderer.contains(pattern, ignoreCase = true)
        }
    }
    
    private fun detectReduceMotion(): Boolean {
        return try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            // If animation scale is 0 or very low, reduce motion is enabled
            scale < 0.1f
        } catch (e: Exception) {
            false
        }
    }
}
