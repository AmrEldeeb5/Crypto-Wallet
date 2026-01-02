/**
 * AnimatedCryptoBackground.kt
 *
 * Animated background component for the Valguard splash screen.
 * Renders three pulsing radial gradient orbs (blue, purple, pink) over
 * a vertical gradient background for a premium, dynamic visual effect.
 *
 * The orbs animate at different speeds to create depth and visual interest:
 * - Blue orb: 3-second pulse cycle
 * - Purple orb: 4-second pulse cycle
 * - Pink orb: 5-second pulse cycle
 *
 * All animations use hardware-accelerated Canvas rendering for 60fps performance.
 */
package com.example.valguard.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Animated background with pulsing gradient orbs.
 *
 * Creates a dynamic background effect with three animated radial gradient circles
 * positioned at different locations on the screen. Each orb pulses its opacity
 * at a different rate to create visual depth and interest.
 *
 * The background uses a vertical gradient from Slate950 → Slate900 → Slate950
 * to provide a subtle vignette effect.
 *
 * @param modifier Modifier to be applied to the Canvas
 */
@Composable
fun AnimatedCryptoBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    
    // Orb 1 animation (Blue) - Top-left quadrant
    val orb1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1"
    )
    
    // Orb 2 animation (Purple) - Bottom-right quadrant
    val orb2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2"
    )
    
    // Orb 3 animation (Pink) - Center
    val orb3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb3"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        // Background gradient (Slate950 → Slate900 → Slate950)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Slate950,
                    Slate900,
                    Slate950
                )
            )
        )
        
        // Blue orb (top-left quadrant)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Blue500.copy(alpha = orb1Alpha),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.25f, size.height * 0.25f),
                radius = size.width * 0.5f
            ),
            radius = size.width * 0.5f,
            center = Offset(size.width * 0.25f, size.height * 0.25f)
        )
        
        // Purple orb (bottom-right quadrant)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Purple500.copy(alpha = orb2Alpha),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.75f, size.height * 0.75f),
                radius = size.width * 0.5f
            ),
            radius = size.width * 0.5f,
            center = Offset(size.width * 0.75f, size.height * 0.75f)
        )
        
        // Pink orb (center)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Pink500.copy(alpha = orb3Alpha),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.5f, size.height * 0.5f),
                radius = size.width * 0.45f
            ),
            radius = size.width * 0.45f,
            center = Offset(size.width * 0.5f, size.height * 0.5f)
        )
    }
}
