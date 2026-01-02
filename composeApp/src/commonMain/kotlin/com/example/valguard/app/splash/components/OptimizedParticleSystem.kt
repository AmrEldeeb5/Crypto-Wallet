package com.example.valguard.app.splash.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.valguard.app.splash.domain.DeviceCapabilities
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Optimized particle system with device capability scaling.
 * Reduces particle count and speed based on device performance.
 */
@Composable
fun OptimizedParticleSystem(
    modifier: Modifier = Modifier,
    capabilities: DeviceCapabilities
) {
    // Calculate particle count based on capabilities
    val particleCount = when {
        capabilities.reduceMotionEnabled -> 0
        capabilities.isLowRam -> Random.nextInt(15, 21) // 15-20 particles
        else -> Random.nextInt(30, 41) // 30-40 particles
    }
    
    // Don't render if reduce motion is enabled
    if (capabilities.reduceMotionEnabled || particleCount == 0) {
        return
    }
    
    // Particle speed: 30% slower than V1
    // V1 was 400ms, V2 is ~570ms (400 * 1.43)
    val streamDuration = 570
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    val particleColors = remember {
        listOf(
            Color(0xFF60A5FA), // Blue400
            Color(0xFFC084FC), // Purple400
            Color(0xFFF9A8D4)  // Pink400
        )
    }
    
    val particleConfigs = remember(particleCount) {
        List(particleCount) { index ->
            Triple(
                Random.nextFloat(), // baseY
                Random.nextFloat() * 4f + 3f, // size (3-7px, smaller than V1)
                Random.nextInt(0, 3000) // delay
            )
        }
    }
    
    val particleStates = particleConfigs.mapIndexed { index, (baseY, size, delay) ->
        val animatedX by infiniteTransition.animateFloat(
            initialValue = -0.1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = streamDuration,
                    delayMillis = delay,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "x_$index"
        )
        
        val wobblePhase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000 + (index * 100),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "wobble_$index"
        )
        
        val radians = wobblePhase * (PI / 180.0)
        val wobbleOffset = sin(radians).toFloat() * 0.03f
        
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    delayMillis = delay,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha_$index"
        )
        
        ParticleState(
            x = animatedX,
            baseY = baseY,
            wobbleOffset = wobbleOffset,
            alpha = animatedAlpha,
            size = size,
            color = particleColors[index % particleColors.size]
        )
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particleStates.forEach { particle ->
            if (particle.x >= -0.05f && particle.x <= 1.05f) {
                val currentY = particle.baseY + particle.wobbleOffset
                val centerX = this.size.width * particle.x
                val centerY = this.size.height * currentY
                
                // Reduce blur layers on old GPU
                val blurLayers = if (capabilities.isOldGpu) 2 else 3
                
                for (i in 0 until blurLayers) {
                    val blurRadius = particle.size * (1f + i * 0.3f)
                    val blurAlpha = particle.alpha * (1f - i * 0.3f)
                    
                    drawCircle(
                        color = particle.color.copy(alpha = blurAlpha),
                        radius = blurRadius,
                        center = Offset(x = centerX, y = centerY)
                    )
                }
            }
        }
    }
}
