package com.example.valguard.app.splash.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.valguard.app.splash.domain.DeviceCapabilities

/**
 * Simplified background with max 2 orbs.
 * Respects reduce motion and scales for old GPUs.
 */
@Composable
fun SimplifiedBackground(
    modifier: Modifier = Modifier,
    capabilities: DeviceCapabilities
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    
    // Blue orb opacity animation (0.1 → 0.15 over 4s)
    val blueOrbAlpha by if (capabilities.reduceMotionEnabled) {
        // Freeze at mid-opacity
        infiniteTransition.animateFloat(
            initialValue = 0.125f,
            targetValue = 0.125f,
            animationSpec = infiniteRepeatable(
                animation = tween(1),
                repeatMode = RepeatMode.Restart
            ),
            label = "blue_alpha"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 4000,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "blue_alpha"
        )
    }
    
    // Purple orb opacity animation (0.12 → 0.18 over 5s)
    val purpleOrbAlpha by if (capabilities.reduceMotionEnabled) {
        // Freeze at mid-opacity
        infiniteTransition.animateFloat(
            initialValue = 0.15f,
            targetValue = 0.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1),
                repeatMode = RepeatMode.Restart
            ),
            label = "purple_alpha"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0.12f,
            targetValue = 0.18f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 5000,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "purple_alpha"
        )
    }
    
    // Reduce orb radius by 30% if old GPU
    val radiusMultiplier = if (capabilities.isOldGpu) 0.7f else 1.0f
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Blue orb (top-left quadrant)
        val blueOrbRadius = 400f * radiusMultiplier
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2563EB).copy(alpha = blueOrbAlpha),
                    Color.Transparent
                ),
                center = Offset(width * 0.25f, height * 0.25f),
                radius = blueOrbRadius
            ),
            radius = blueOrbRadius,
            center = Offset(width * 0.25f, height * 0.25f)
        )
        
        // Purple orb (bottom-right quadrant)
        val purpleOrbRadius = 450f * radiusMultiplier
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF7C3AED).copy(alpha = purpleOrbAlpha),
                    Color.Transparent
                ),
                center = Offset(width * 0.75f, height * 0.75f),
                radius = purpleOrbRadius
            ),
            radius = purpleOrbRadius,
            center = Offset(width * 0.75f, height * 0.75f)
        )
    }
}
