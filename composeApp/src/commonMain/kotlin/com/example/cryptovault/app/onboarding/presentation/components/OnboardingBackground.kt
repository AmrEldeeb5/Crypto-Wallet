/**
 * OnboardingBackground.kt
 *
 * Animated background component for the onboarding screens.
 * Creates an immersive visual experience with pulsing gradient orbs
 * and floating cryptocurrency symbols.
 *
 * Features:
 * - Gradient background (slate-950 to slate-900)
 * - Animated pulsing orbs (blue, purple, pink)
 * - Floating crypto symbols with drift animations
 * - Respects reduce motion accessibility setting
 *
 * @see OnboardingScreen for usage context
 */
package com.example.cryptovault.app.onboarding.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptovault.theme.LocalCryptoAccessibility
import com.example.cryptovault.theme.LocalCryptoColors
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated background with gradient orbs and floating symbols.
 *
 * Creates an immersive visual backdrop for onboarding screens.
 * Animations are disabled when reduce motion accessibility is enabled.
 *
 * @param modifier Optional modifier for the component
 */
@Composable
fun OnboardingBackground(
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion
    
    // Static alpha values when reduce motion is enabled
    val staticOrb1Alpha = 0.1f
    val staticOrb2Alpha = 0.15f
    val staticOrb3Alpha = 0.05f
    
    // Animated alpha values for orbs
    val orb1Alpha: Float
    val orb2Alpha: Float
    val orb3Alpha: Float
    
    if (reduceMotion) {
        orb1Alpha = staticOrb1Alpha
        orb2Alpha = staticOrb2Alpha
        orb3Alpha = staticOrb3Alpha
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "background")
        
        val animatedOrb1Alpha by infiniteTransition.animateFloat(
            initialValue = 0.05f,
            targetValue = 0.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orb1"
        )
        
        val animatedOrb2Alpha by infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orb2"
        )
        
        val animatedOrb3Alpha by infiniteTransition.animateFloat(
            initialValue = 0.03f,
            targetValue = 0.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orb3"
        )
        
        orb1Alpha = animatedOrb1Alpha
        orb2Alpha = animatedOrb2Alpha
        orb3Alpha = animatedOrb3Alpha
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Canvas for orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background gradient (slate-950 -> slate-900 -> slate-950)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF020617), // slate-950
                        Color(0xFF0F172A), // slate-900
                        Color(0xFF020617)  // slate-950
                    )
                )
            )
            
            // Blue orb - top left
            drawAnimatedOrb(
                center = Offset(size.width * 0.25f, size.height * 0.25f),
                radius = size.width * 0.4f,
                color = colors.accentBlue500,
                alpha = orb1Alpha
            )
            
            // Purple orb - bottom right
            drawAnimatedOrb(
                center = Offset(size.width * 0.75f, size.height * 0.75f),
                radius = size.width * 0.4f,
                color = colors.accentPurple500,
                alpha = orb2Alpha
            )
            
            // Pink orb - center
            drawAnimatedOrb(
                center = Offset(size.width * 0.5f, size.height * 0.5f),
                radius = size.width * 0.35f,
                color = colors.accentPink500,
                alpha = orb3Alpha
            )
        }
        
        // Floating crypto symbols (skip if reduce motion enabled)
        if (!reduceMotion) {
            FloatingCryptoSymbols()
        }
    }
}

/**
 * Draws an animated gradient orb on the canvas.
 *
 * @param center Center position of the orb
 * @param radius Radius of the orb
 * @param color Base color for the orb
 * @param alpha Current alpha value (animated)
 */
private fun DrawScope.drawAnimatedOrb(
    center: Offset,
    radius: Float,
    color: Color,
    alpha: Float
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                Color.Transparent
            ),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

/**
 * Data class holding properties for a floating symbol animation.
 *
 * @property symbol The crypto symbol character to display
 * @property x Initial X position
 * @property y Initial Y position
 * @property size Font size in sp
 * @property alpha Base alpha value
 * @property durationY Vertical animation duration in ms
 * @property durationX Horizontal animation duration in ms
 * @property amplitudeY Vertical movement range
 * @property amplitudeX Horizontal movement range
 * @property delay Animation start delay in ms
 */
private data class SymbolData(
    val symbol: String,
    val x: Int,
    val y: Int,
    val size: Int,
    val alpha: Float,
    val durationY: Int,
    val durationX: Int,
    val amplitudeY: Float,
    val amplitudeX: Float,
    val delay: Int
)

/**
 * Renders floating cryptocurrency symbols across the background.
 * Generates 20 symbols with randomized positions and animation properties.
 */
@Composable
private fun FloatingCryptoSymbols() {
    val symbols = listOf("₿", "Ξ", "◎", "₳", "Ł", "◈", "⟐", "✦", "⬡", "◇")
    
    // Generate random positions and properties for 20 symbols
    val symbolData = remember {
        (0 until 20).map { index ->
            SymbolData(
                symbol = symbols[index % symbols.size],
                x = Random.nextInt(10, 350),
                y = Random.nextInt(50, 750),
                size = Random.nextInt(24, 48),
                alpha = Random.nextFloat() * 0.06f + 0.02f, // 0.02 to 0.08
                durationY = Random.nextInt(4000, 8000),
                durationX = Random.nextInt(5000, 10000),
                amplitudeY = Random.nextFloat() * 30f + 15f, // 15 to 45
                amplitudeX = Random.nextFloat() * 20f + 10f, // 10 to 30
                delay = Random.nextInt(0, 2000)
            )
        }
    }
    
    symbolData.forEach { data ->
        FloatingSymbol(data)
    }
}

/**
 * Individual floating symbol with drift and pulse animations.
 *
 * @param data Animation properties for this symbol
 */
@Composable
private fun FloatingSymbol(data: SymbolData) {
    val infiniteTransition = rememberInfiniteTransition(label = "symbol_${data.x}_${data.y}")
    
    // Vertical floating animation
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = data.amplitudeY,
        animationSpec = infiniteRepeatable(
            animation = tween(data.durationY, delayMillis = data.delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y_${data.x}_${data.y}"
    )
    
    // Horizontal drifting animation
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -data.amplitudeX / 2,
        targetValue = data.amplitudeX / 2,
        animationSpec = infiniteRepeatable(
            animation = tween(data.durationX, delayMillis = data.delay, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x_${data.x}_${data.y}"
    )
    
    // Alpha pulsing animation
    val alphaMultiplier by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(data.durationY + 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_${data.x}_${data.y}"
    )
    
    Text(
        text = data.symbol,
        fontSize = data.size.sp,
        color = Color.White,
        modifier = Modifier
            .offset(
                x = (data.x + offsetX).dp,
                y = (data.y + offsetY).dp
            )
            .alpha(data.alpha * alphaMultiplier)
    )
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun OnboardingBackgroundPreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        OnboardingBackground()
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun FloatingSymbolPreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Box(modifier = Modifier.size(100.dp).background(Color(0xFF0F172A)), contentAlignment = Alignment.Center) {
            FloatingSymbol(
                data = SymbolData(
                    symbol = "₿",
                    x = 0, y = 0, size = 48, alpha = 0.8f,
                    durationY = 3000, durationX = 4000,
                    amplitudeY = 20f, amplitudeX = 15f, delay = 0
                )
            )
        }
    }
}
