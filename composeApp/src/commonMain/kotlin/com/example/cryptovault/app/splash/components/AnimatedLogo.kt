/**
 * AnimatedLogo.kt
 *
 * Animated logo component for the CryptoVault splash screen.
 * Features multiple animated layers including rotating rings, pulsing scales,
 * and a vault icon with an upward trending arrow.
 *
 * The logo consists of:
 * - Outer ring: Continuously rotating (360° over 20s)
 * - Middle ring: Pulsing scale animation (1.0 → 1.1 over 3s)
 * - Logo core: Subtle pulse animation (1.0 → 1.05 over 2s)
 * - Vault icon: Gradient-filled rounded square with arrow
 *
 * All animations run concurrently using hardware-accelerated rendering
 * for smooth 60fps performance.
 */
package com.example.cryptovault.app.splash.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cryptovault.theme.*

/**
 * Animated logo with multiple concurrent animation layers.
 *
 * Creates a premium animated logo effect with:
 * - Three concentric rings with different animations
 * - Gradient-filled vault icon
 * - Trending up arrow in the center
 * - All animations synchronized for visual harmony
 *
 * The logo is 200dp in size and centered within its container.
 * All animations use infinite transitions with reverse repeat modes
 * for continuous, smooth motion.
 *
 * @param modifier Modifier to be applied to the logo container
 */
@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    
    // Logo scale pulse (1.0 → 1.05 over 2s)
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Rotating ring (0° → 360° over 20s)
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Pulsing ring (1.0 → 1.1 over 3s)
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring"
    )
    
    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rotating outer ring (200dp)
        Box(
            modifier = Modifier
                .size(200.dp)
                .rotate(rotation)
                .border(
                    width = 4.dp,
                    color = Blue500.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        )
        
        // Pulsing middle ring (180dp)
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(ringScale)
                .border(
                    width = 2.dp,
                    color = Purple500.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
        
        // Main logo container (150dp)
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(scale)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Slate800.copy(alpha = 0.5f),
                            Slate900.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    width = 1.dp,
                    color = Slate700.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Vault icon (100dp)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Blue600, Purple600)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inner vault (80dp)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Blue700, Purple700)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Lock/Arrow circle (50dp)
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.4f),
                                        Slate200.copy(alpha = 0.6f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Animated arrow
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Growth",
                            tint = Emerald400,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}
