/**
 * WelcomeStep.kt
 *
 * First step of the onboarding flow introducing users to CryptoVault.
 * Displays the app logo, welcome message, and key feature highlights.
 *
 * Features:
 * - Animated sparkle icon with pulse effect
 * - Gradient text for app name
 * - Staggered feature card animations
 * - Respects reduce motion accessibility
 *
 * @see OnboardingScreen for the parent container
 * @see welcomeFeatures for the displayed features
 */
package com.example.cryptovault.app.onboarding.presentation.steps

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptovault.app.onboarding.domain.OnboardingFeature
import com.example.cryptovault.app.onboarding.domain.welcomeFeatures
import com.example.cryptovault.theme.LocalCryptoAccessibility
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoTypography
import kotlinx.coroutines.delay

/**
 * Welcome step content for onboarding.
 *
 * Displays the app introduction with animated header and
 * feature highlight cards.
 *
 * @param modifier Optional modifier for the component
 */
@Composable
fun WelcomeStep(
    modifier: Modifier = Modifier
) {
    LocalCryptoColors.current
    LocalCryptoTypography.current
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        WelcomeHeader()

        
        // Feature highlight cards
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            welcomeFeatures.forEachIndexed { index, feature ->
                WelcomeFeatureCard(
                    feature = feature,
                    index = index,
                    animateIn = !reduceMotion
                )
            }
        }
    }
}

/**
 * Header section with animated icon and welcome text.
 *
 * Displays the sparkle icon with pulse animation, gradient app name,
 * and tagline. Animations are disabled when reduce motion is enabled.
 *
 * @param modifier Optional modifier for the component
 */
@Composable
fun WelcomeHeader(
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion

    // Animated values - static when reduce motion is enabled
    val iconScale: Float
    val pingScale: Float
    val pingAlpha: Float

    if (reduceMotion) {
        iconScale = 1f
        pingScale = 1f
        pingAlpha = 0f
    } else {
        val infiniteTransition = rememberInfiniteTransition()

        // Pulse animation for icon
        val animatedIconScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000),
                repeatMode = RepeatMode.Reverse
            )
        )
        iconScale = animatedIconScale

        // Ping animation for outer circle
        val animatedPingScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Restart
            )
        )
        pingScale = animatedPingScale

        val animatedPingAlpha by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Restart
            )
        )
        pingAlpha = animatedPingAlpha
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sparkles icon section



            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(iconScale)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(colors.accentBlue500, colors.accentPurple500)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✦", fontSize = 48.sp, color = Color.White)
            }




        Text(
            text = "Welcome to CryptoVault",
            style = typography.titleLarge.copy(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colors.accentBlue400,
                        colors.accentPurple400,
                        colors.accentPink400
                    )
                )
            ),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )



        Text(
            text = "Your premium crypto tracking companion",
            style = typography.bodyLarge,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Feature card for the welcome step.
 *
 * Compact horizontal card showing feature icon, title, and description.
 * Supports staggered entrance animation.
 *
 * @param feature The feature to display
 * @param index Position for staggered animation (default 0)
 * @param animateIn Whether to animate entrance (default true)
 * @param modifier Optional modifier for the component
 */
@Composable
fun WelcomeFeatureCard(
    feature: OnboardingFeature,
    index: Int = 0,
    animateIn: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val accessibility = LocalCryptoAccessibility.current
    val reduceMotion = accessibility.reduceMotion


    // Staggered animation
    var isVisible by remember { mutableStateOf(!animateIn || reduceMotion) }

    LaunchedEffect(animateIn, reduceMotion) {
        if (animateIn && !reduceMotion) {
            delay(index * 150L)
            isVisible = true
        }
    }

    val alpha = if (isVisible) 1f else 0f
    val cardShape = RoundedCornerShape(16.dp)
    // React: bg-slate-800/50 border border-slate-700/50
    val slateBackground = Color(0xFF1E293B).copy(alpha = 0.5f)
    val slateBorder = Color(0xFF334155).copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clip(cardShape)
            .background(slateBackground)
            .border(
                width = 1.dp,
                color = slateBorder,
                shape = cardShape
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon with gradient background (rounded-xl)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(feature.gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = feature.iconType.emoji,
                    fontSize = 16.sp
                )
            }



            Column(
                modifier = Modifier.fillMaxWidth(),

            ) {
                Text(
                    text = feature.title,
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = feature.description,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun WelcomeHeaderPreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Box(modifier = Modifier.background(Color(0xFF0F172A)).padding(24.dp)) {
            WelcomeHeader()
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun WelcomeFeatureCardPreview() {
    val feature = welcomeFeatures[0]
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Box(
            modifier = Modifier.background(Color(0xFF0F172A)).padding(16.dp)
        ) {
            WelcomeFeatureCard(feature = feature)
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun WelcomeStepPreview() {
    com.example.cryptowallet.theme.CoinRoutineTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
        ) {
            WelcomeStep()
        }
    }
}
