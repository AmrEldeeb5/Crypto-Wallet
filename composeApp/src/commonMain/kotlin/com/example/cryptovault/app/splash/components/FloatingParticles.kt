package com.example.cryptovault.app.splash.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data class representing a streaming particle.
 *
 * @param x Current X position (0.0 to 1.0, percentage of screen width)
 * @param baseY Base Y position (0.0 to 1.0, percentage of screen height)
 * @param wobbleOffset Current wobble offset for vertical variation
 * @param alpha Current opacity (0.0 to 1.0)
 * @param size Particle radius in pixels
 * @param color Particle color
 */
data class ParticleState(
    val x: Float,
    val baseY: Float,
    val wobbleOffset: Float,
    val alpha: Float,
    val size: Float,
    val color: Color
)

/**
 * Streaming particles effect.
 *
 * Creates particles that stream rapidly from left to right across the screen.
 * Each particle has slight vertical wobble for organic motion.
 *
 * Performance: Optimized for 50 particles at 60fps. Reduce particleCount
 * on lower-end devices if needed.
 *
 * @param modifier Modifier to be applied to the Canvas
 * @param particleCount Number of particles to render (default: 50)
 */
@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 50
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    // Particle colors (blue, purple, pink)
    val particleColors = remember {
        listOf(
            Color(0xFF60A5FA), // Blue400
            Color(0xFFC084FC), // Purple400
            Color(0xFFF9A8D4)  // Pink400
        )
    }
    
    // Generate particle configurations once
    val particleConfigs = remember {
        List(particleCount) { index ->
            Triple(
                Random.nextFloat(), // baseY (vertical position)
                Random.nextFloat() * 6f + 4f, // size (4-10px) - bigger and varied
                Random.nextInt(0, 4000) // delay
            )
        }
    }
    
    // Create animated states for all particles
    val particleStates = particleConfigs.mapIndexed { index, (baseY, size, delay) ->
        val duration = 500 // 0.5 seconds to cross screen (even faster)
        
        // Horizontal streaming animation (left → right, continuous)
        val animatedX by infiniteTransition.animateFloat(
            initialValue = -0.1f, // Start slightly off-screen left
            targetValue = 1.1f,   // End slightly off-screen right
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = duration,
                    delayMillis = delay,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart // Loop back to start
            ),
            label = "x_$index"
        )
        
        // Vertical wobble animation (subtle sine wave)
        val wobblePhase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000 + (index * 100), // Vary wobble speed
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "wobble_$index"
        )
        
        // Calculate wobble offset (±3% of screen height)
        // Convert degrees to radians manually since java.lang.Math is not available in commonMain
        val radians = wobblePhase * (PI / 180.0)
        val wobbleOffset = sin(radians).toFloat() * 0.03f
        
        // Opacity animation (fade in/out for depth)
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.6f,
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
            // Only draw if particle is on screen
            if (particle.x >= -0.05f && particle.x <= 1.05f) {
                val currentY = particle.baseY + particle.wobbleOffset
                val centerX = this.size.width * particle.x
                val centerY = this.size.height * currentY
                
                // Draw blurred particle (multiple overlapping circles for blur effect)
                val blurLayers = 3
                for (i in 0 until blurLayers) {
                    val blurRadius = particle.size * (1f + i * 0.3f)
                    val blurAlpha = particle.alpha * (1f - i * 0.3f)
                    
                    drawCircle(
                        color = particle.color.copy(alpha = blurAlpha),
                        radius = blurRadius,
                        center = Offset(
                            x = centerX,
                            y = centerY
                        )
                    )
                }
            }
        }
    }
}
